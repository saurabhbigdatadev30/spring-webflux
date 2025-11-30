package com.vinsguru.playground.sec04.exceptions;

import reactor.core.publisher.Mono;

 // This is an Exception Factory class that will throw all the Custom Runtime Exceptions
public class ApplicationExceptions {

    public static <T> Mono<T> customerNotFound(Integer customerId){
        return Mono.error(new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> missingName(){
        return Mono.error(new InvalidInputException("Name is required"));
    }

     public static <T> Mono<T> missingEmail(){
         return Mono.error(new InvalidInputException("email cannot be null "));
     }
     public static <T> Mono<T> invalidEmailFormat(){
         return Mono.error(new InvalidInputException("Invalid email format"));
     }

// Added for refactored InvalidInputException
     public static <T> Mono<T> restrictedCharacters(String input){
         return Mono.error(new InvalidInputExceptionRefactored("Restricted characters in input" ,input));
     }



}


