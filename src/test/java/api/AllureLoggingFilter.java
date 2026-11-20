package api;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import utils.AllureAttachments;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Attaches the request that was actually sent and the response that was actually received
 * to the Allure report, for every API call made through {@link ApiSpecs#base()}.
 * <p>
 * Sensitive values (passwords, tokens, Authorization/Cookie headers) are masked here,
 * in one place, so individual tests never have to build report attachments by hand.
 */
public class AllureLoggingFilter implements Filter {

    private static final String MASK = "***";

    private static final Set<String> SENSITIVE_HEADERS =
            Set.of("authorization", "cookie", "set-cookie");

    // Matches "password": "...", "access_token": "..." etc. (handles escaped quotes in the value)
    private static final Pattern SENSITIVE_JSON_FIELD = Pattern.compile(
            "(\"(?:password|access_token|refresh_token|token)\"\\s*:\\s*\")(?:[^\"\\\\]|\\\\.)*(\")",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {

        AllureAttachments.attachText(
                "Request: " + requestSpec.getMethod() + " " + requestSpec.getURI(),
                describeRequest(requestSpec)
        );

        Response response = context.next(requestSpec, responseSpec);

        AllureAttachments.attachText(
                "Response: " + response.statusLine(),
                describeResponse(response)
        );

        return response;
    }

    private static String describeRequest(FilterableRequestSpecification request) {
        StringBuilder text = new StringBuilder();
        text.append(request.getMethod()).append(' ').append(request.getURI()).append('\n');
        appendHeaders(text, request.getHeaders());

        Object body = request.getBody();
        if (body != null) {
            text.append('\n').append(maskSensitiveFields(prettify(bodyAsString(body))));
        }
        return text.toString();
    }

    private static String describeResponse(Response response) {
        StringBuilder text = new StringBuilder();
        text.append(response.statusLine()).append('\n');
        text.append("Response time: ").append(response.time()).append(" ms\n");
        appendHeaders(text, response.getHeaders());

        String body = prettyResponseBody(response);
        text.append('\n').append(body.isBlank() ? "<empty body>" : maskSensitiveFields(body));
        return text.toString();
    }

    private static void appendHeaders(StringBuilder text, Headers headers) {
        for (Header header : headers) {
            String value = SENSITIVE_HEADERS.contains(header.getName().toLowerCase())
                    ? MASK
                    : header.getValue();
            text.append(header.getName()).append(": ").append(value).append('\n');
        }
    }

    private static String bodyAsString(Object body) {
        if (body instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return String.valueOf(body);
    }

    private static String prettify(String json) {
        try {
            return JsonPath.from(json).prettify();
        } catch (RuntimeException e) {
            return json; // not JSON - keep as is
        }
    }

    private static String prettyResponseBody(Response response) {
        try {
            return response.asPrettyString();
        } catch (RuntimeException e) {
            return response.asString();
        }
    }

    private static String maskSensitiveFields(String body) {
        return SENSITIVE_JSON_FIELD.matcher(body).replaceAll("$1" + MASK + "$2");
    }
}
