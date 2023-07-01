package com.eldar.jsonschema.validators.providers;

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DefaultValueProvider {

    public static Object getDefaultValue(Class<?> type) {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return 0;
        }
        if (type.equals(long.class) || type.equals(Long.class)) {
            return 0L;
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return 0.0;
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return 0.0f;
        }
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return false;
        }
        if (type.equals(String.class)) {
            return "";
        }
        if (List.class.isAssignableFrom(type)) {
            return Collections.emptyList();
        }
        if (Set.class.isAssignableFrom(type)) {
            return Collections.emptySet();
        }
        if (Map.class.isAssignableFrom(type)) {
            return Collections.emptyMap();
        }
        // Other object
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
