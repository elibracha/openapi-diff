package com.github.elibracha.compare;

import com.github.elibracha.model.ChangedResponse;
import com.github.elibracha.model.DiffContext;
import com.github.elibracha.utils.ChangedUtils;
import com.github.elibracha.utils.RefPointer;
import com.github.elibracha.utils.RefType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.HashSet;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 28/12/17.
 */
public class ResponseDiff extends ReferenceDiffCache<ApiResponse, ChangedResponse> {
    private static RefPointer<ApiResponse> refPointer = new RefPointer<>(RefType.RESPONSES);
    private OpenApiDiff openApiDiff;
    private Components leftComponents;
    private Components rightComponents;

    public ResponseDiff(OpenApiDiff openApiDiff) {
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

    public Optional<ChangedResponse> diff(ApiResponse left, ApiResponse right, DiffContext context) {
        return cachedDiff(new HashSet<>(), left, right, left.get$ref(), right.get$ref(), context);
    }

    @Override
    protected Optional<ChangedResponse> computeDiff(
            HashSet<String> refSet, ApiResponse left, ApiResponse right, DiffContext context) {
        left = refPointer.resolveRef(leftComponents, left, left.get$ref());
        right = refPointer.resolveRef(rightComponents, right, right.get$ref());

        ChangedResponse changedResponse = new ChangedResponse(left, right, context);
        openApiDiff
                .getMetadataDiff()
                .diff(left.getDescription(), right.getDescription(), context)
                .ifPresent(changedResponse::setDescription);
        openApiDiff
                .getContentDiff()
                .diff(left.getContent(), right.getContent(), context)
                .ifPresent(changedResponse::setContent);
        openApiDiff
                .getHeadersDiff()
                .diff(left.getHeaders(), right.getHeaders(), context)
                .ifPresent(changedResponse::setHeaders);
        openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context)
                .ifPresent(changedResponse::setExtensions);
        return ChangedUtils.isChanged(changedResponse);
    }
}
