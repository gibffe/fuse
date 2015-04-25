package com.sulaco.fuse.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"unchecked"})
public class PrimitiveConverters {

    static Map<Class<?>, Function<String, ?>> converters = new HashMap<>();
    static {
        converters.put(Integer.class , Integer::valueOf);
        converters.put(Long.class    , Long::valueOf);
        converters.put(Double.class  , Double::valueOf);
        converters.put(Float.class   , Float::valueOf);
        converters.put(Boolean.class , Boolean::valueOf);
    }

    public static <T> T convert(String value, Class<T> target) {

        T converted =
            (T) converters.getOrDefault(target, v -> null)
                          .apply(value);

        return converted;
    }

}
