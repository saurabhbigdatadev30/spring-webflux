package com.vinsguru.playground.sec04.exceptions;

public class InvalidInputExceptionRefactored extends RuntimeException
{
    private static final String MESSAGE = "Invalid input: %s";

    public InvalidInputExceptionRefactored(String message, String field) {
        super(message + ": " + field);
       //  To DO super(message.formatted(field));
    }

    // For cases where the field is null.
    public InvalidInputExceptionRefactored(String message) {
        super(message);
    }


}
