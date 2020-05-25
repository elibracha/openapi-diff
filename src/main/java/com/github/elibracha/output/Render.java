package com.github.elibracha.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.elibracha.model.ChangedOpenApi;

public interface Render {

    String render(ChangedOpenApi diff) throws JsonProcessingException;
}
