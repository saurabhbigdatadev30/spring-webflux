package com.vinsguru.playground.sec04.validator;

import com.vinsguru.playground.sec02.entity.Customer;
import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.exceptions.ApplicationExceptions;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Slf4j
public class RequestValidator {

    private static final Set<String> SOCIAL_EMAIL_DOMAINS = Set.of(
            "gmail.com",
            "yahoo.com",
            "yahoo.in",
            "outlook.com",
            "hotmail.com"
            );
    /**
        The NAME_ALLOWED_CHARS is a variable that holds the implementation of a Predicate<String>
        functional interface using a lambda expression. The Predicate<String> interface represents
        a single argument function that takes a String as input and returns a boolean value.
        It is invoked when NAME_ALLOWED_CHARS.test(...) is invoked.
     */
    private static final Predicate<String> NAME_ALLOWED_CHARS =
            (name) -> {
                           log.info("name check for allowed characters, name={}", name);
                            return name != null && name.matches("^[a-zA-Z]+$");
            };

    /*
       validate() is a Higher Order Function that  returns a Function<T,R> that takes a
       T = Mono<CustomerDto> and R =  Mono<CustomerDto>.
     */

    public static UnaryOperator<Mono<CustomerDto>> validate() {
        return customerDto ->
        {
           return  customerDto
                   .doOnNext(dto -> log.info("Validate the incoming request dto: = {}", dto))
                   .filter(hasName())  // Method reference to custom validation for the above lambda
                   .switchIfEmpty(ApplicationExceptions.missingName())
                   .filter(isNameValid())
                   .switchIfEmpty(ApplicationExceptions.invalidNameFormat())
                   .filter(hasEmail())
                   .switchIfEmpty(ApplicationExceptions.missingEmail())
                   .filter(isEmailValidByLambda())
                   .switchIfEmpty(ApplicationExceptions.invalidEmailFormat());
        };
    }

	/**
     FROM COPILOT

    1) Correct. validateUnderstandingUnderTheHood() is a higher-order function in the sense that it returns a function = (UnaryOperator<Mono<CustomerDto>>).
	             It returns a lambda that implements functional interface UnaryOperator.

    2) Correct. UnaryOperator<Mono<CustomerDto>> means the returned lambda has the shape :- Mono<CustomerDto> -> f() -> Mono<CustomerDto>.

    3) Correct. The Mono<CustomerDto> parameter is not an argument to validateUnderstandingUnderTheHood() itself, it’s the argument of the returned lambda.

    4) Correct. validateUnderstandingUnderTheHood() does not take an argument like validateUnderstandingUnderTheHood(Mono<CustomerDto> ...);
                **** it’s validateUnderstandingUnderTheHood() with no params, returning a function that later accepts the Mono<CustomerDto>.

    5) Understanding the Invocation to Higher Order Function validateUnderstandingUnderTheHood();
              (customerDtoMono) ->   RequestValidator
                                    .validateUnderstandingUnderTheHood()  // returns UnaryOperator<Mono<CustomerDto>>
                                    .apply(customerDtoMono)               // applies it to the input Mono

	  */

    public static UnaryOperator<Mono<CustomerDto>> validateUnderstandingUnderTheHood(){
        return customerDto -> {
            return customerDto
                    .doOnNext(dto -> log.info("Validate the incoming request dto the Lambda way: = {}", dto))
                    .filter(dto -> hasName().test(dto))
                    .switchIfEmpty(ApplicationExceptions.missingName())
                    .filter(dto -> isNameValid().test(dto))
                    .switchIfEmpty(ApplicationExceptions.invalidNameFormat())
                    .filter(dto -> hasEmail().test(dto))
                    .switchIfEmpty(ApplicationExceptions.missingEmail())
                    .filter(dto -> isEmailValidByLambda().test(dto))
                    .switchIfEmpty(ApplicationExceptions.invalidEmailFormat());
        };
    }

    private static Predicate<CustomerDto> hasName() {
        return dto -> {
            log.info("Validating if name is not null dto: {}", dto);
            return dto!=null && Objects.nonNull(dto.name());
        };
    }

    private static Predicate<CustomerDto> isNameValid() {
        return dto -> {
            String name = dto == null ? null : dto.name();
            log.info("Validating name does not contain special characters, name={}", name);
            return name != null && NAME_ALLOWED_CHARS.test(name.trim());
        };
    }


    private static Predicate<CustomerDto> hasEmail(){
        return dto -> {
            log.info("check if email is valid, dto = {}", dto);
            return dto != null && Objects.nonNull(dto.email());
        };
    }

    // Using lambda expression , we validate the email host name
   private static Predicate<CustomerDto> isEmailValidByLambda(){
        return customerDto -> {
            return isHostNameValid(customerDto);
        };
   }

   // The above lambda expression can be replaced by method reference & validate the email host name
    public static Predicate<CustomerDto>isEmailValid(){
        return RequestValidator::isHostNameValid;
    }

    private static boolean isHostNameValid(CustomerDto dto) {
        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        // Basic sanity: must contain exactly one '@' and a non-empty local part/domain.
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }
        // Extract domain part from the email address & then validate it.
        String domain = email.substring(at + 1);

        // Must look like a domain (very lightweight check).
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }

        // Reject social/free email providers.
        return !SOCIAL_EMAIL_DOMAINS.contains(domain);
    }
}

