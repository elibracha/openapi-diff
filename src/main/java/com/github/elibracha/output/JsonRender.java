package com.github.elibracha.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.elibracha.model.Changed;
import com.github.elibracha.model.ChangedOpenApi;
import com.github.elibracha.model.ChangedOperation;

import java.util.List;

public class JsonRender implements Render {
    protected ChangedOpenApi diff;

    @Override
    public String render(ChangedOpenApi diff) throws JsonProcessingException {
        this.diff=diff;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(diff.getChangedOperations());
        return json;

    }

}
