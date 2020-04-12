package com.github.elibracha.output;

import com.github.elibracha.model.ChangedOpenApi;

public interface Render {

  String render(ChangedOpenApi diff);
}
