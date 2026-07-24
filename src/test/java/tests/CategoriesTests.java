package tests;
import base.BaseTest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CategoryRequest;
import models.CategoryResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestDataGenerator;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CategoriesTests extends BaseTest {


    // =======================
    // GET /categories
    // =======================

    @Test
    public void shouldReturn200WhenGettingCategories() {

        given()
                .when()
                .get("/categories")
                .then()
                .statusCode(200);

    }

    @Test
    public void shouldReturnCategoriesWithValidData() {
        Response response = given()
                            .when()
                            .get("/categories");

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

    @Test
    public void shouldReturn404WhenResourceIsNotFound(){

        given()
                .when()
                .get("/category")
                .then()
                .statusCode(404);

    }

    @Test
    public void shouldNotAllowPutMethodOnCategoriesEndpoint(){

        given()
                .when()
                .put("/categories")
                .then()
                .statusCode(405);

    }

    @Test
    public void shouldNotAllowPatchMethodOnCategoriesEndpoint(){

        given()
                .when()
                .patch("/categories")
                .then()
                .statusCode(405);

    }


    // =======================
    // POST /categories
    // =======================

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

    @Test
    public void shouldCreateCategoryWithValidResponseData() {

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");

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



    @Test
        public void shouldReturn422WhenCreatingCategoryWithInvalidData() {
            CategoryRequest request = new CategoryRequest();
            request.setSlug("invalid-slug");


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/categories")
                .then()
                .statusCode(422);

    }

    @Test
    public void shouldReturn409WhenCreatingCategoryWithExistingSlug(){

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/categories")
                .then()
                .statusCode(201);


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/categories")
                .then()
                .statusCode(409);

    }

    // ==============================
    // PUT /categories/{categoryId}
    // ==============================


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


        createResponse.then()
                .statusCode(201);


        int categoryId =
                createResponse.jsonPath()
                        .getInt("id");


        CategoryRequest updateRequest = new CategoryRequest();
                updateRequest.setName("Updated Category");
                updateRequest.setSlug("updated-category");


        Response updateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(updateRequest)
                        .when()
                        .put("/categories/" + categoryId);


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


        getResponse.then()
                .statusCode(200);


        Assert.assertEquals(
                getResponse.jsonPath().getString("name"),
                "Updated Category"
        );


        Assert.assertEquals(
                getResponse.jsonPath().getString("slug"),
                "updated-category"
        );
    }

    // ================================
    // DELETE /categories/{categoryId}
    // ================================

    @Test
    public void shouldDeleteCategorySuccessfully() {

        // Create category
        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        Response createResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/categories");


        int categoryId =
                createResponse.jsonPath()
                        .getInt("id");


        // Delete category
        given()
                .when()
                .delete("/categories/" + categoryId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get("/categories/tree/" + categoryId)
                .then()
                .statusCode(404);
    }


}
