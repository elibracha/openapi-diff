package com.github.elibracha.openapi.test;

import org.junit.jupiter.api.Test;

import static com.github.elibracha.openapi.test.TestUtils.assertOpenApiChangedEndpoints;

/**
 * Created by adarsh.sharma on 03/01/18.
 */
public class RequestDiffTest {
    private final String OPENAPI_DOC1 = "request_diff_1.yaml";
    private final String OPENAPI_DOC2 = "request_diff_2.yaml";

    @Test
    public void testDiffDifferent() {
        assertOpenApiChangedEndpoints(OPENAPI_DOC1, OPENAPI_DOC2);
    }
}
