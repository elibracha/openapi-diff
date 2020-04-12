package com.github.elibracha.compare;

import com.github.elibracha.model.ChangedMetadata;
import com.github.elibracha.model.DiffContext;
import com.github.elibracha.utils.ChangedUtils;

import io.swagger.v3.oas.models.Components;
import java.util.Optional;

public class MetadataDiff {

  private Components leftComponents;
  private Components rightComponents;
  private OpenApiDiff openApiDiff;

  public MetadataDiff(OpenApiDiff openApiDiff) {
    this.openApiDiff = openApiDiff;
    this.leftComponents =
        openApiDiff.getOldSpecOpenApi() != null
            ? openApiDiff.getOldSpecOpenApi().getComponents()
            : null;
    this.rightComponents =
        openApiDiff.getNewSpecOpenApi() != null
            ? openApiDiff.getNewSpecOpenApi().getComponents()
            : null;
  }

  public Optional<ChangedMetadata> diff(String left, String right, DiffContext context) {
    return ChangedUtils.isChanged(new ChangedMetadata().setLeft(left).setRight(right));
  }
}
