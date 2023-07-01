package com.eldar.jsonschema.validators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
/**
 * Validate all parameters in the method
 * to be not null and not empty (if applicable)
 */
public @interface ValidatedMethod {
    boolean allowNull() default false;
    boolean allowEmpty() default false;
    boolean throwException() default false;

}