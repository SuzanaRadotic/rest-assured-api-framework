package tests;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.Method;
import io.restassured.response.Response;
import models.CategoryRequest;
import models.CategoryResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.TestDataGenerator;

import java.util.List;

@Epic("Practice Software Testing API")
@Feature("Categories")
public class CategoriesTests extends BaseTest {

    // =======================
    // GET /categories
    // =======================
    @Story("GET /categories")
    @Test
    public void shouldReturn200WhenGettingCategories() {

        Response response = categories.getAll();

        response.then()
                .statusCode(200);
    }

    @Story("GET /categories")
    @Test
    public void shouldReturnCategoriesWithValidData() {

        Response response = categories.getAll();

        // Get response as list of categories
        List<CategoryResponse> categoryList =
                response.jsonPath().getList("$", CategoryResponse.class);

        // Verify response is not empty
        Assert.assertFalse(
                categoryList.isEmpty(),
                "Categories response should not be empty"
        );

        // Validate each category
        for (CategoryResponse category : categoryList) {

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
    public void shouldReturn404WhenResourceIsNotFound() {

        Response response = categories.sendRaw(Method.GET, "/category");

        response.then()
                .statusCode(404);
    }

    @Story("GET /categories")
    @Test
    public void shouldNotAllowPutMethodOnCategoriesEndpoint() {

        Response response = categories.sendRaw(Method.PUT, "/categories");

        response.then()
                .statusCode(405);
    }

    @Story("GET /categories")
    @Test
    public void shouldNotAllowPatchMethodOnCategoriesEndpoint() {

        Response response = categories.sendRaw(Method.PATCH, "/categories");

        response.then()
                .statusCode(405);
    }

    // =======================
    // POST /categories
    // =======================
    @Story("POST /categories")
    @Test
    public void shouldReturn201WhenCreatingCategory() {

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        Response response = categories.create(request);
        trackCreatedCategory(response);

        response.then().statusCode(201);

        Assert.assertNotNull(response.jsonPath().get("id"));
    }

    @Story("POST /categories")
    @Test
    public void shouldCreateCategoryWithValidResponseData() {

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        Response response = categories.create(request);
        trackCreatedCategory(response);

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

        Response response = categories.create(request);
        // safety net: if the API wrongly accepts invalid data, the category is still removed
        trackCreatedCategory(response);

        response.then().statusCode(422);
    }

    @Story("POST /categories")
    @Test
    public void shouldReturn409WhenCreatingCategoryWithExistingSlug() {

        CategoryRequest request =
                TestDataGenerator.createCategoryRequest(null);

        // Arrange: the first category is created (and asserted) by the helper
        createCategoryForTest(request);

        Response duplicateResponse = categories.create(request);
        trackCreatedCategory(duplicateResponse);

        duplicateResponse.then().statusCode(409);
    }

    // ==============================
    // PUT /categories/{categoryId}
    // ==============================

    @Story("PUT /categories/{categoryId}")
    @Test
    public void shouldUpdateCategorySuccessfully() {

        int categoryId = createCategoryForTest(
                TestDataGenerator.createCategoryRequest(null)
        );

        CategoryRequest updateRequest =
                TestDataGenerator.createUpdatedCategoryRequest(null);

        Response updateResponse =
                categories.update(categoryId, updateRequest);

        updateResponse.then()
                .statusCode(200);

        Assert.assertTrue(
                updateResponse.jsonPath().getBoolean("success")
        );

        // Verify update through GET
        Response getResponse = categories.getTree(categoryId);

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

        // Arrange: create category
        int categoryId = createCategoryForTest(
                TestDataGenerator.createCategoryRequest(null)
        );

        // Act: delete category
        categories.delete(categoryId)
                .then()
                .statusCode(204);

        // Verify deletion
        categories.getTree(categoryId)
                .then()
                .statusCode(404);
    }
}
