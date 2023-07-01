package com.eldar.jsonschema.validators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
/**
 * Validate a method parameter using a given JSON Schema.
 * for example @ValidParam(ValidationSchema.NON_EMPTY_STRING)
 */
public @interface ValidatedParam {
    /**
     Provide an explicit JSON Schema validation.
     **/
    String value() default "";
    boolean throwException() default false;
}