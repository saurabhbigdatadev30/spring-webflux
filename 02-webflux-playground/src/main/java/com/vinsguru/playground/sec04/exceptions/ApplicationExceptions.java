package com.vinsguru.playground.sec04.exceptions;

import reactor.core.publisher.Mono;

 // This is an Exception Factory class that will throw all the Custom Runtime Exceptions
public class ApplicationExceptions {

    public static <T> Mono<T> customerNotFound(Integer id){
        return Mono.error(new CustomerNotFoundException(id));
    }

    public static <T> Mono<T> missingName(){
        return Mono.error(new InvalidInputException("Name is required"));
    }

    public static <T> Mono<T> missingValidEmail(){
        return Mono.error(new InvalidInputException("email is not valid"));
    }

     public static <T> Mono<T> missingEmail(){
         return Mono.error(new InvalidInputException("email cannot be null "));
     }

}
