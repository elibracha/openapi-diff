package com.github.elibracha.openapi.test;

import com.github.elibracha.OpenApiCompare;
import com.github.elibracha.model.ChangedOpenApi;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentDiffTest {

    private final String OPENAPI_DOC1 = "content_diff_1.yaml";
    private final String OPENAPI_DOC2 = "content_diff_2.yaml";

    @Test
    public void testContentDiffWithOneEmptyMediaType() {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);
        assertThat(changedOpenApi.isIncompatible()).isTrue();
    }

    @Test
    public void testContentDiffWithEmptyMediaTypes() {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC1);
        assertThat(changedOpenApi.isUnchanged()).isTrue();
    }

    @Test
    public void testSameContentDiff() {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC2, OPENAPI_DOC2);
        assertThat(changedOpenApi.isUnchanged()).isTrue();
    }
}
