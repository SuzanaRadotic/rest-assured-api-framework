package tests;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.LoginRequest;
import models.LoginResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestConstants;

import static io.restassured.RestAssured.given;

public class AuthenticationTests extends BaseTest {

    @Test
    public void shouldReturnAccessTokenWhenLoggingIn()
    {
        LoginRequest loginRequest = new LoginRequest(
                                        TestConstants.CUSTOMER_EMAIL,
                                        TestConstants.CUSTOMER_PASSWORD
                                    );

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(loginRequest)
                        .when()
                        .post("/users/login");

        response.then().statusCode(200);

        LoginResponse loginResponse =
                response.as(LoginResponse.class);

        Assert.assertNotNull(loginResponse.getAccess_token(), "Access token should not be null");

        Assert.assertEquals(loginResponse.getToken_type(), "bearer");

    }


    @Test
    public void shouldReturnCurrentUserWhenAuthenticated() {

        LoginRequest loginRequest =
                new LoginRequest(
                        TestConstants.CUSTOMER_EMAIL,
                        TestConstants.CUSTOMER_PASSWORD
                );

        Response loginApiResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(loginRequest)
                        .when()
                        .post("/users/login");

        loginApiResponse.then()
                .statusCode(200);

        LoginResponse login =
                loginApiResponse.as(LoginResponse.class);

        Assert.assertNotNull(
                login.getAccess_token(),
                "Access token should not be null"
        );


        Response userResponse =
                given()
                        .header(
                                "Authorization",
                                "Bearer " + login.getAccess_token()
                        )
                        .when()
                        .get("/users/me");

        userResponse.then()
                .statusCode(200);

        Assert.assertEquals(
                userResponse.jsonPath().getString("email"),
                TestConstants.CUSTOMER_EMAIL
        );
    }
}
