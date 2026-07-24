package utils;

import models.CategoryRequest;
import net.datafaker.Faker;

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

        String baseName = CATEGORY_NAMES[
                faker.number().numberBetween(0, CATEGORY_NAMES.length)
                ] + "_" + System.currentTimeMillis();


        String slug = baseName
                .toLowerCase()
                .replace(" ", "-");


        return new CategoryRequest(
                baseName,
                slug,
                parentId
        );
    }
}
