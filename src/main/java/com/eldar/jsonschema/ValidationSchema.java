package com.eldar.jsonschema;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ValidationSchema {

    public static final String NOT_NULL = """
            {
              "not": {
                "type": "null"
              }
            }
            """;

    public static final String NON_EMPTY_ARRAY = """
            {
               "type": "array",
               "minItems": 1
             }
            """;

    public static final String NON_EMPTY_OBJECT = """
            {
               "type": "object",
               "minProperties": 1
            }
            """;

    public static final String NON_EMPTY_STRING = """
            {
               "type": "string",
               "minLength": 1
             }
            """;
}
