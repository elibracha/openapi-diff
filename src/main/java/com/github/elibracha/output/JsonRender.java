package com.github.elibracha.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.elibracha.model.ChangedOpenApi;
public class JsonRender implements Render {
    protected ChangedOpenApi diff;
    @Override
    public String render(ChangedOpenApi diff) throws JsonProcessingException {
        this.diff = diff;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diff);
        return json;

    }

}
