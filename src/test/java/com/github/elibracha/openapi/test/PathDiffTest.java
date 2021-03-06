package com.github.elibracha.openapi.test;

import org.junit.jupiter.api.Test;

import static com.github.elibracha.openapi.test.TestUtils.assertOpenApiAreEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PathDiffTest {

    private final String OPENAPI_PATH1 = "path_1.yaml";
    private final String OPENAPI_PATH2 = "path_2.yaml";
    private final String OPENAPI_PATH3 = "path_3.yaml";

    @Test
    public void testEqual() {
        assertOpenApiAreEquals(OPENAPI_PATH1, OPENAPI_PATH2);
    }

    @Test
    public void testMultiplePathWithSameSignature() {
        assertThrows(
                IllegalArgumentException.class, () -> assertOpenApiAreEquals(OPENAPI_PATH1, OPENAPI_PATH3));
    }
}
