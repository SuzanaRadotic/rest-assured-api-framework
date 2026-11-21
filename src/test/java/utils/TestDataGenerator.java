package utils;

import models.CategoryRequest;
import net.datafaker.Faker;

import java.util.UUID;

public class TestDataGenerator {

    private static final Faker faker = new Faker();

    private static final String[] CATEGORY_NAMES = {
            "Hand Tools",
            "Power Tools",
            "Measuring Tools",
            "Sharpening Tools",
            "Gardening Tools",
            "Safety Equipment",
            "Painting Tools",
            "Fasteners"
    };

    public static CategoryRequest createCategoryRequest(Integer parentId) {
        String prefix = CATEGORY_NAMES[
                faker.number().numberBetween(0, CATEGORY_NAMES.length)
                ];

        return buildCategoryRequest(prefix, parentId);
    }

    public static CategoryRequest createUpdatedCategoryRequest(Integer parentId) {
        return buildCategoryRequest("Updated Category", parentId);
    }

    private static CategoryRequest buildCategoryRequest(String prefix, Integer parentId) {
        String name = prefix + "_" + uniqueSuffix();

        String slug = name
                .toLowerCase()
                .replace(" ", "-");

        return new CategoryRequest(name, slug, parentId);
    }

    // Random instead of System.currentTimeMillis(): no collisions if tests run in parallel
    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
