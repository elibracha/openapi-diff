package com.github.elibracha.openapi.test;

import com.github.elibracha.OpenApiCompare;
import com.github.elibracha.model.ChangedOpenApi;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

public class TestUtils {
    public static final Logger LOG = getLogger(TestUtils.class);

    public static void assertOpenApiAreEquals(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
        assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
        assertThat(changedOpenApi.getChangedOperations()).isEmpty();
    }

    public static void assertOpenApiChangedEndpoints(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
        assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
        assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();
    }

    public static void assertOpenApiBackwardCompatible(
            String oldSpec, String newSpec, boolean isDiff) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        assertThat(changedOpenApi.isCompatible()).isTrue();
    }

    public static void assertOpenApiBackwardIncompatible(String oldSpec, String newSpec) {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(oldSpec, newSpec);
        LOG.info("Result: {}", changedOpenApi.isChanged().getValue());
        assertThat(changedOpenApi.isIncompatible()).isTrue();
    }
}
