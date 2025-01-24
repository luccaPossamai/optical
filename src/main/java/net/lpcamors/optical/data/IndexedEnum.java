package net.lpcamors.optical.data;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IndexedEnum<T extends Enum<T>> {

    public final boolean autoLowerCase;
    public final Map<String, T> named;
    public final List<T> indexed;

    public IndexedEnum(boolean autoLowerCase, @SuppressWarnings("unchecked") T... values) {
        this.autoLowerCase = autoLowerCase;
        named = ImmutableMap.<String, T>builder()
            .putAll(Arrays
                .stream(values)
                .map(e -> Map.entry(processName(e.name()), e))
                .toList()
            )
            .build();
        indexed = ImmutableList.copyOf(values);
    }

    private String processName(String name) {
        if (autoLowerCase) {
            return name.toLowerCase(Locale.ROOT);
        }
        return name;
    }

    public boolean indexInBound(int index) {
        return index >= 0 && index < indexed.size();
    }

    public T byIndex(int index) {
        if (!indexInBound(index)) {
            return null;
        }
        return indexed.get(index);
    }

    public T byName(String name) {
        return named.get(processName(name));
    }

    public T fromJson(JsonElement element) {
        if (!(element instanceof JsonPrimitive primitive)) {
            return null;
        }
        if (primitive.isNumber()) {
            return byIndex(primitive.getAsInt());
        } else if (primitive.isString()) {
            return byName(primitive.getAsString());
        }
        return null;
    }
}
