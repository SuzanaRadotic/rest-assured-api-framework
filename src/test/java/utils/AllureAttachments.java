package utils;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class AllureAttachments {

    private AllureAttachments() {
    }

    public static void attachJson(String name, String content) {
        Allure.addAttachment(
                name,
                "application/json",
                new ByteArrayInputStream(
                        content.getBytes(StandardCharsets.UTF_8)
                ),
                ".json"
        );
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(
                name,
                "text/plain",
                new ByteArrayInputStream(
                        content.getBytes(StandardCharsets.UTF_8)
                ),
                ".txt"
        );
    }
}
