package api;

import io.restassured.response.Response;
import models.LoginRequest;
import utils.TestConstants;

import static io.restassured.RestAssured.given;

/**
 * Authentication endpoints. Returns raw responses so each test decides what to assert.
 */
public class AuthClient {

    public Response login(LoginRequest request) {
        return given(ApiSpecs.base())
                .body(request)
                .post("/users/login");
    }

    public Response loginAsCustomer() {
        return login(new LoginRequest(
                TestConstants.CUSTOMER_EMAIL,
                TestConstants.CUSTOMER_PASSWORD
        ));
    }

    public Response getCurrentUser(String accessToken) {
        return given(ApiSpecs.base())
                .header("Authorization", "Bearer " + accessToken)
                .get("/users/me");
    }
}
