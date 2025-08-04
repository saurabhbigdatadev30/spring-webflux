package com.vinsguru.playground.sec04.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer [id=%d] is not found";

    public CustomerNotFoundException(Integer id) {
        // super("Customer with ID '%d' was not found.".formatted(customerId));
           super(MESSAGE.formatted(id));
    }

}
