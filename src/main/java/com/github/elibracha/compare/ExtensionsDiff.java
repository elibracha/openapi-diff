package com.github.elibracha.compare;

import com.github.elibracha.model.Change;
import com.github.elibracha.model.Changed;
import com.github.elibracha.model.ChangedExtensions;
import com.github.elibracha.model.DiffContext;
import com.github.elibracha.utils.ChangedUtils;
import com.github.elibracha.utils.Copy;

import java.util.*;
import java.util.function.Function;

public class ExtensionsDiff {
    private final OpenApiDiff openApiDiff;

    private ServiceLoader<ExtensionDiff> extensionsLoader = ServiceLoader.load(ExtensionDiff.class);
    private List<ExtensionDiff> extensionsDiff = new ArrayList<>();

    public ExtensionsDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
        this.extensionsLoader.reload();
        for (ExtensionDiff anExtensionsLoader : this.extensionsLoader) {
            extensionsDiff.add(anExtensionsLoader);
        }
    }

    public boolean isParentApplicable(
            Change.Type type, Object parent, Map<String, Object> extensions, DiffContext context) {
        if (extensions.size() == 0) {
            return true;
        }
        return extensions.entrySet().stream()
                .map(
                        entry ->
                                executeExtension(
                                        entry.getKey(),
                                        extensionDiff ->
                                                extensionDiff.isParentApplicable(type, parent, entry.getValue(), context)))
                .allMatch(aBoolean -> aBoolean.orElse(true));
    }

    public Optional<ExtensionDiff> getExtensionDiff(String name) {
        return extensionsDiff.stream().filter(diff -> ("x-" + diff.getName()).equals(name)).findFirst();
    }

    public <T> Optional<T> executeExtension(String name, Function<ExtensionDiff, T> predicate) {
        return getExtensionDiff(name)
                .map(extensionDiff -> extensionDiff.setOpenApiDiff(openApiDiff))
                .map(predicate);
    }

    public Optional<ChangedExtensions> diff(Map<String, Object> left, Map<String, Object> right) {
        return this.diff(left, right, null);
    }

    public Optional<ChangedExtensions> diff(
            Map<String, Object> left, Map<String, Object> right, DiffContext context) {
        left = Copy.copyMap(left);
        right = Copy.copyMap(right);
        ChangedExtensions changedExtensions = new ChangedExtensions(left, Copy.copyMap(right), context);
        for (String key : left.keySet()) {
            Object leftValue = left.get(key);
            if (right.containsKey(key)) {
                Object rightValue = right.remove(key);
                executeExtensionDiff(key, Change.changed(leftValue, rightValue), context)
                        .filter(Changed::isDifferent)
                        .ifPresent(changed -> changedExtensions.getChanged().put(key, changed));
            } else {
                executeExtensionDiff(key, Change.removed(leftValue), context)
                        .filter(Changed::isDifferent)
                        .ifPresent(changed -> changedExtensions.getMissing().put(key, changed));
            }
        }
        right.forEach(
                (key, value) ->
                        executeExtensionDiff(key, Change.added(value), context)
                                .filter(Changed::isDifferent)
                                .ifPresent(changed -> changedExtensions.getIncreased().put(key, changed)));
        return ChangedUtils.isChanged(changedExtensions);
    }

    private Optional<Changed> executeExtensionDiff(String name, Change change, DiffContext context) {
        return executeExtension(name, diff -> diff.setOpenApiDiff(openApiDiff).diff(change, context));
    }
}
