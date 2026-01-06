package com.vinsguru.playground.sec04.exceptions;

public class InvalidInputException extends RuntimeException {
    /*
        Generic message will be passed to the constructor, that describes the invalid input scenarios for all
        inputs
     */
    public InvalidInputException(String message) {
        super(message);
    }

}
