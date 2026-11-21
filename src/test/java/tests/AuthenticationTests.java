package tests;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import models.LoginResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestConstants;

@Epic("Practice Software Testing API")
@Feature("Authentication")
public class AuthenticationTests extends BaseTest {

    @Test
    public void shouldReturnAccessTokenWhenLoggingIn() {

        Response response = auth.loginAsCustomer();

        response.then()
                .statusCode(200);

        LoginResponse loginResponse =
                response.as(LoginResponse.class);

        Assert.assertNotNull(
                loginResponse.getAccessToken(),
                "Access token should not be null"
        );

        Assert.assertEquals(
                loginResponse.getTokenType(),
                "bearer"
        );
    }

    @Test
    public void shouldReturnCurrentUserWhenAuthenticated() {

        Response loginResponse = auth.loginAsCustomer();

        loginResponse.then()
                .statusCode(200);

        LoginResponse login =
                loginResponse.as(LoginResponse.class);

        Assert.assertNotNull(
                login.getAccessToken(),
                "Access token should not be null"
        );

        Response userResponse =
                auth.getCurrentUser(login.getAccessToken());

        userResponse.then()
                .statusCode(200);

        Assert.assertEquals(
                userResponse.jsonPath().getString("email"),
                TestConstants.CUSTOMER_EMAIL
        );
    }
}
