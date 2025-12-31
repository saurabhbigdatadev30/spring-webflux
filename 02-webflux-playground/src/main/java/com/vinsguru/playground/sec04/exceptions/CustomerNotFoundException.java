package com.vinsguru.playground.sec04.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer with [customerId=%d] is not found";

    public CustomerNotFoundException(Integer customerId) {
           super("Customer with [customerId=%d] is not found".formatted(customerId));
    }


}
