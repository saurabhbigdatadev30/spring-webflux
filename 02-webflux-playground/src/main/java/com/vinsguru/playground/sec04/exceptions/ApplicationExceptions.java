package com.vinsguru.playground.sec04.exceptions;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import reactor.core.publisher.Mono;

 /*
   Exception Factory class that will throw Custom Runtime Exceptions , when the validation fails
   in RequestValidator
  */
public class ApplicationExceptions {

    public static <T> Mono<T> customerNotFound(Integer customerId){
        return Mono.error(new CustomerNotFoundException(customerId));
    }

    public static <T> Mono<T> missingName(){
        return Mono.error(new InvalidInputException("Name is required"));
    }

     public static Mono<CustomerDto> invalidNameFormat() {
         return Mono.error(new InvalidInputException("Name must contain only alphabets and spaces"));
     }

     public static <T> Mono<T> missingEmail(){
         return Mono.error(new InvalidInputException("email is required "));
     }
     public static <T> Mono<T> invalidEmailFormat(){
         return Mono.error(new InvalidInputException("Invalid email format"));
     }

     // Generic method to handle any of the invalid input field name , providing the field name as parameter
     public static <T> Mono<T> invalidInputFieldName(String fieldName){
         return Mono.error(new InvalidInputExceptionRefactored(fieldName));
     }


 }


