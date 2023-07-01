package com.eldar.jsonschema.exception;

import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class UnprocessableObject extends RuntimeException {
    Set<ValidationMessage> validations;

    public UnprocessableObject(Set<ValidationMessage> validations, String message) {
        super(message);
        this.validations = validations;
        log.error(message + validations);
    }
}
