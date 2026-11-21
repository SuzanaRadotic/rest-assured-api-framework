package base;

import api.AuthClient;
import api.CategoryClient;
import io.restassured.response.Response;
import models.CategoryRequest;
import org.testng.annotations.AfterMethod;
import utils.AllureAttachments;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Base class for all API tests.
 * <p>
 * Provides the API clients and a per-test cleanup mechanism: anything a test creates on the
 * shared environment is registered for deletion and removed in {@link #cleanUpTestData()},
 * even when the test fails. Cleanup is best-effort and never fails the test itself;
 * a problem during cleanup is attached to the Allure report instead.
 */
public abstract class BaseTest {

    protected final AuthClient auth = new AuthClient();
    protected final CategoryClient categories = new CategoryClient();

    private record Cleanup(String description, Runnable action) {
    }

    // ThreadLocal keeps cleanup lists separate if tests are later run in parallel.
    private final ThreadLocal<Deque<Cleanup>> cleanups =
            ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * Registers an action to run after the current test. Actions run in reverse order
     * (last registered, first executed), so dependent resources are removed before their parents.
     */
    protected void runAfterTest(String description, Runnable action) {
        cleanups.get().push(new Cleanup(description, action));
    }

    protected void deleteCategoryAfterTest(int categoryId) {
        runAfterTest("Delete category " + categoryId, () -> {
            int status = categories.delete(categoryId).statusCode();
            // 404 is fine: the test may already have deleted the category itself
            if (status != 204 && status != 404) {
                throw new IllegalStateException("Unexpected status " + status);
            }
        });
    }

    /**
     * Call right after every create request, before asserting anything, so the category is
     * cleaned up even if an assertion fails. Does nothing when the response carries no id
     * (e.g. a 422/409 error response).
     */
    protected void trackCreatedCategory(Response createResponse) {
        try {
            Integer id = createResponse.jsonPath().get("id");
            if (id != null) {
                deleteCategoryAfterTest(id);
            }
        } catch (RuntimeException e) {
            // no JSON body / no usable id -> nothing was created
        }
    }

    /**
     * Arrange step for tests that need an existing category as a precondition.
     * Fails with a clear message if the precondition itself cannot be created.
     */
    protected int createCategoryForTest(CategoryRequest request) {
        Response response = categories.create(request);
        trackCreatedCategory(response);
        response.then().statusCode(201);
        return response.jsonPath().getInt("id");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUpTestData() {
        Deque<Cleanup> pending = cleanups.get();
        try {
            while (!pending.isEmpty()) {
                Cleanup cleanup = pending.pop();
                try {
                    cleanup.action().run();
                } catch (RuntimeException e) {
                    AllureAttachments.attachText(
                            "Cleanup failed: " + cleanup.description(),
                            e.toString()
                    );
                }
            }
        } finally {
            cleanups.remove();
        }
    }
}
