package com.github.elibracha.compare;

import com.github.elibracha.model.Change;
import com.github.elibracha.model.Changed;
import com.github.elibracha.model.DiffContext;

public interface ExtensionDiff {

  ExtensionDiff setOpenApiDiff(OpenApiDiff openApiDiff);

  String getName();

  Changed diff(Change extension, DiffContext context);

  default boolean isParentApplicable(
      Change.Type type, Object object, Object extension, DiffContext context) {
    return true;
  }
}
