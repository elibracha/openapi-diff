package com.github.elibracha.openapi.test;

import com.github.elibracha.OpenApiCompare;
import com.github.elibracha.model.ChangedHeaders;
import com.github.elibracha.model.ChangedOpenApi;
import com.github.elibracha.model.ChangedResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ResponseHeaderDiffTest {

    private final String OPENAPI_DOC1 = "header_1.yaml";
    private final String OPENAPI_DOC2 = "header_2.yaml";

    @Test
    public void testDiffDifferent() {
        ChangedOpenApi changedOpenApi = OpenApiCompare.fromLocations(OPENAPI_DOC1, OPENAPI_DOC2);

        assertThat(changedOpenApi.getNewEndpoints()).isEmpty();
        assertThat(changedOpenApi.getMissingEndpoints()).isEmpty();
        assertThat(changedOpenApi.getChangedOperations()).isNotEmpty();

        Map<String, ChangedResponse> changedResponses =
                changedOpenApi.getChangedOperations().get(0).getApiResponses().getChanged();
        assertThat(changedResponses).isNotEmpty();
        assertThat(changedResponses).containsKey("200");
        ChangedHeaders changedHeaders = changedResponses.get("200").getHeaders();
        assertThat(changedHeaders.isDifferent()).isTrue();
        assertThat(changedHeaders.getChanged()).hasSize(1);
        assertThat(changedHeaders.getIncreased()).hasSize(1);
        assertThat(changedHeaders.getMissing()).hasSize(1);
    }
}
