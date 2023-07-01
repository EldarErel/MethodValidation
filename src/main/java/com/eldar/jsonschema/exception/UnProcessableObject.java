package com.eldar.jsonschema.exception;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class UnProcessableObject extends RuntimeException {
    private final Set<ValidationMessage> validations;

    public UnProcessableObject(Set<ValidationMessage> validations, String message) {
        super(message);
        this.validations = validations;
    }
}
