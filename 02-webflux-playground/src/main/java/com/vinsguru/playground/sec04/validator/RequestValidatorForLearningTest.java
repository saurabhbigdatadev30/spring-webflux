package com.vinsguru.playground.sec04.validator;

import com.vinsguru.playground.sec04.dto.CustomerDto;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class RequestValidatorForLearningTest {

private static final Set<String> HOST_NAMES_BLOCKED = Set.of
        ("outlook.com" ,"gmail.com" , "hotmail.com");

private static Predicate<String> ALLOWED_NAMES_CHARS() {
    return name -> {
        return name != null && name.matches("^[a-zA-Z]+$");
    };
    }

private static Predicate<CustomerDto> hasName(){
    return customerDto -> {
        return customerDto.name()!=null ;
    };
}

private static Predicate<CustomerDto> nameHasAllowedChars(){
    return customerDto -> {
        return ALLOWED_NAMES_CHARS().test(customerDto.name());
    };
}

private static Predicate<CustomerDto> hasEmail(){
    return customerDto -> {
        return Objects.nonNull(customerDto.email());
    };
}

/*
 1. emailHasValidHostName() is a Higher-Order Function because it returns a function (Predicate<CustomerDto>) rather than a primitive value.

 2. emailHasValidHostName() itself does not implements the  Predicate, but instead, it returns a lambda expression that implements the
    Predicate<CustomerDto> functional interface.

 3. The returned lambda accepts a CustomerDto as input and provides the implementation of the test(CustomerDto) method.

 4.  The lambda is not executed when emailHasValidHostName() is called, but only when test() is invoked on the returned Predicate.

    Internally, the lambda is translated by the compiler into an implementation equivalent to an anonymous class overriding Predicate.test().
 */
private static Predicate<CustomerDto> emailHasValidHostName(Set<String> blockedHostNames){
    return customerDto -> {
        String email = customerDto.email();
        if (email!=null && email.contains("@")){
            String hostName = email.substring(email.indexOf("@") + 1);
            return !blockedHostNames.contains(hostName);
        }
        return false;
    };
}

// Combined Predicates to form a more complex validation rule for corporate email validation
private static Predicate<CustomerDto> isCorporateEmailValid(){
    return hasEmail().and(emailHasValidHostName(HOST_NAMES_BLOCKED));
}


    public static void main(String[] args) {
        CustomerDto customerDto1 = new CustomerDto(122 , "abc" , "abc@abc.com");
        CustomerDto customerDto2 = new CustomerDto(122 , "abc" , "abc@gmail.com");
        System.out.println(isCorporateEmailValid().test(customerDto1));
        System.out.println(isCorporateEmailValid().test(customerDto2));
    }
}
