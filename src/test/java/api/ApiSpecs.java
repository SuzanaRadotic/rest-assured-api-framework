package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;

/**
 * Single place where the shared request configuration lives.
 * Replaces the global RestAssured.baseURI, so nothing depends on static mutable state
 * and different environments / parallel runs do not interfere with each other.
 */
public final class ApiSpecs {

    private ApiSpecs() {
    }

    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new AllureLoggingFilter())
                .build();
    }
}
