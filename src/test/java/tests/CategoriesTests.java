package tests;
import base.BaseTest;

import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CategoryRequest;
import models.CategoryResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureAttachments;
import utils.TestDataGenerator;

import java.util.List;

import static io.restassured.RestAssured.given;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Practice Software Testing API")
@Feature("Categories")
public class CategoriesTests extends BaseTest {


    // =======================
    // GET /categories
    // =======================
    @Story("GET /categories")
    @Test
    public void shouldReturn200WhenGettingCategories() {

        Response response =
                given()
                        .when()
                        .get("/categories");

        response.then()
                .statusCode(200);

    }

    @Story("GET /categories")
    @Test
    public void shouldReturnCategoriesWithValidData() {
        Response response = given()
                            .when()
                            .get("/categories");

         AllureAttachments.attachResponse(response);

        // Get response as list of categories
        List<CategoryResponse> categories =
                response.jsonPath().getList("$", CategoryResponse.class);


        // Verify response is not empty
        Assert.assertFalse(
                categories.isEmpty(),
                "Categories response should not be empty"
        );

        // Validate each category
        for (CategoryResponse category : categories) {

            Assert.assertNotNull(
                    category.getId(),
                    "Category id should not be null"
            );

            Assert.assertNotNull(
                    category.getName(),
                    "Category name should not be null"
            );

            Assert.assertNotNull(
                    category.getSlug(),
                    "Category slug should not be null"
            );
        }
    }

    @Story("GET /categories")
    @Test
    public void shouldReturn404WhenResourceIsNotFound(){

        Response response =
                given()
                        .when()
                        .get("/category");

        AllureAttachments.attachResponse(response);

        response.then()
                .statusCode(404);

    }

    @Story("GET /categories")
    @Test
    public void shouldNotAllowPutMethodOnCategoriesEndpoint() {

        Response response =
                given()
                        .when()
                        .put("/categories");

        AllureAttachments.attachResponse(response);

        response.then()
                .statusCode(405);

    }

    @Story("GET /categories")
    @Test
    public void shouldNotAllowPatchMethodOnCategoriesEndpoint() {

        Response response =
                given()
                        .when()
                        .patch("/categories");

        AllureAttachments.attachResponse(response);

        response.then()
                .statusCode(405);

    }


    // =======================
    // POST /categories
    // =======================
    @Story("POST /categories")
    @Test
    public void shouldReturn201WhenCreatingCategory(){

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        response.then().statusCode(201);

        Assert.assertNotNull(response.jsonPath().get("id"));
    }

    @Story("POST /categories")
    @Test
    public void shouldCreateCategoryWithValidResponseData() {

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        AllureAttachments.attachJson(
                "Create category request",
                """
                {
                  "name": "%s",
                  "slug": "%s",
                  "parentId": %s
                }
                """.formatted(
                        request.getName(),
                        request.getSlug(),
                        request.getParentId()
                )
        );

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        AllureAttachments.attachResponse(response);

        response.then().statusCode(201);

        CategoryResponse categoryResponse =
                response.as(CategoryResponse.class);

        // Validate generated category data
        Assert.assertNotNull(
                categoryResponse.getId(),
                "Category id should not be null"
        );

        Assert.assertEquals(
                categoryResponse.getName(),
                request.getName(),
                "Category name should match request"
        );

        Assert.assertEquals(
                categoryResponse.getSlug(),
                request.getSlug(),
                "Category slug should match request"
        );

        Assert.assertEquals(
                        categoryResponse.getParentId(),
                        request.getParentId(),
                "Parent id should match request"
        );
    }


    @Story("POST /categories")
    @Test
        public void shouldReturn422WhenCreatingCategoryWithInvalidData() {

        CategoryRequest request = new CategoryRequest();
        request.setSlug("invalid-slug");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        AllureAttachments.attachJson(
                "Create invalid category request",
                """
                {
                  "slug": "%s"
                }
                """.formatted(
                       request.getSlug()
                )
        );

        AllureAttachments.attachResponse(response);

        response.then().statusCode(422);
    }

    @Story("POST /categories")
    @Test
    public void shouldReturn409WhenCreatingCategoryWithExistingSlug(){

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);


        Response firstResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        AllureAttachments.attachResponse(firstResponse);

        firstResponse.then()
                .statusCode(201);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        AllureAttachments.attachResponse(response);

        response.then().statusCode(409);

    }

    // ==============================
    // PUT /categories/{categoryId}
    // ==============================

    @Story("PUT /categories/{categoryId}")
    @Test
    public void shouldUpdateCategorySuccessfully(){

        CategoryRequest createRequest =
                TestDataGenerator.createCategoryRequest(null);


        Response createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(createRequest)
                        .when()
                        .post("/categories");

        AllureAttachments.attachJson(
                "Create category request",
                """
                {
                  "name": "%s",
                  "slug": "%s",
                  "parentId": %s
                }
                """.formatted(
                        createRequest.getName(),
                        createRequest.getSlug(),
                        createRequest.getParentId()
                )
        );

        AllureAttachments.attachResponse(createResponse);


        createResponse.then()
                .statusCode(201);


        int categoryId =
                createResponse.jsonPath()
                        .getInt("id");


        CategoryRequest updateRequest =
                TestDataGenerator.createCategoryRequest(null);

        String updatedName = "Updated Category_" + System.currentTimeMillis();

        String updatedSlug =
                updatedName.toLowerCase()
                        .replace(" ", "-");

        updateRequest.setName(updatedName);
        updateRequest.setSlug(updatedSlug);


        Response updateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(updateRequest)
                        .when()
                        .put("/categories/" + categoryId);

        AllureAttachments.attachJson(
                "Update category request",
                """
                {
                  "name": "%s",
                  "slug": "%s",
                  "parentId": %s
                }
                """.formatted(
                        updateRequest.getName(),
                        updateRequest.getSlug(),
                        updateRequest.getParentId()
                )
        );

        AllureAttachments.attachResponse(updateResponse);

        updateResponse.then()
                .statusCode(200);


        Assert.assertTrue(
                updateResponse.jsonPath().getBoolean("success")
        );

        // Verify update through GET
        Response getResponse =
                given()
                        .when()
                        .get("/categories/tree/" + categoryId);

        AllureAttachments.attachResponse(getResponse);

        getResponse.then()
                .statusCode(200);

        Assert.assertEquals(
                getResponse.jsonPath().getString("name"),
                updateRequest.getName(),
                "Category name should match updated value"
        );

        Assert.assertEquals(
                getResponse.jsonPath().getString("slug"),
                updateRequest.getSlug(),
                "Category slug should match updated value"
        );
    }

    // ================================
    // DELETE /categories/{categoryId}
    // ================================

    @Story("DELETE /categories/{categoryId}")
    @Test
    public void shouldDeleteCategorySuccessfully() {

        // Create category
        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        AllureAttachments.attachJson(
                "Create category request",
                """
                {
                  "name": "%s",
                  "slug": "%s",
                  "parentId": %s
                }
                """.formatted(
                        request.getName(),
                        request.getSlug(),
                        request.getParentId()
                )
        );

        Response createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

        AllureAttachments.attachResponse(createResponse);

        int categoryId =
                createResponse.jsonPath()
                        .getInt("id");


        // Delete category
        Response deleteResponse =
                given()
                        .when()
                        .delete("/categories/" + categoryId);

        AllureAttachments.attachResponse(deleteResponse);

        deleteResponse.then()
                .statusCode(204);


        // Verify deletion
        Response verifyResponse =
                given()
                        .when()
                        .get("/categories/tree/" + categoryId);

        AllureAttachments.attachResponse(verifyResponse);

        verifyResponse.then()
                .statusCode(404);
    }
}
