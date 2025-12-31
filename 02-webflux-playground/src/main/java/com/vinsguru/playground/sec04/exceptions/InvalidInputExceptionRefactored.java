package com.vinsguru.playground.sec04.exceptions;

public class InvalidInputExceptionRefactored extends RuntimeException
{
    private static final String MESSAGE = "input [field=%s] is not valid";

    public InvalidInputExceptionRefactored(String fieldName) {
        super("input [fieldName=%s] is not valid".formatted(fieldName));
    }
}

