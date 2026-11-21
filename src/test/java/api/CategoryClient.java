package api;

import io.restassured.http.Method;
import io.restassured.response.Response;
import models.CategoryRequest;

import static io.restassured.RestAssured.given;

/**
 * Category endpoints. Returns raw responses so each test decides what to assert.
 */
public class CategoryClient {

    private static final String CATEGORIES = "/categories";

    public Response getAll() {
        return given(ApiSpecs.base())
                .get(CATEGORIES);
    }

    public Response getTree(int categoryId) {
        return given(ApiSpecs.base())
                .get(CATEGORIES + "/tree/" + categoryId);
    }

    public Response create(CategoryRequest request) {
        return given(ApiSpecs.base())
                .body(request)
                .post(CATEGORIES);
    }

    public Response update(int categoryId, CategoryRequest request) {
        return given(ApiSpecs.base())
                .body(request)
                .put(CATEGORIES + "/" + categoryId);
    }

    public Response delete(int categoryId) {
        return given(ApiSpecs.base())
                .delete(CATEGORIES + "/" + categoryId);
    }

    /**
     * For protocol-level negative tests (unknown path, unsupported HTTP method)
     * where no typed endpoint method makes sense.
     */
    public Response sendRaw(Method method, String path) {
        return given(ApiSpecs.base())
                .request(method, path);
    }
}
