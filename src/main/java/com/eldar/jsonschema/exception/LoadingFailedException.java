package com.eldar.jsonschema.exception;

public class LoadingFailedException extends RuntimeException {

    public LoadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
