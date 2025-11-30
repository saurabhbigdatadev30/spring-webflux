package com.vinsguru.playground.sec04.validator;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.exceptions.ApplicationExceptions;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Slf4j
public class RequestValidator {

 /*
   The validate method takes a Mono<CustomerDto> as input argument , performs series of validation checks on the
   CustomerDto object & returns a Mono<CustomerDto> ... if all validations pass or an error Mono if any validation fails.
  */
    public static UnaryOperator<Mono<CustomerDto>> validate() {
        // get the incoming customerDto object as input argument & validate the fields of the dto
        return customerDto ->
        {
            return        customerDto
                         .doOnNext(dto -> log.info("Validate the incoming request dto: = {}", dto))
                         .filter(hasName())
                         .switchIfEmpty(ApplicationExceptions.missingName())
                         .filter(hasEmail())
                         .switchIfEmpty(ApplicationExceptions.missingEmail())
                         .filter(hasValidEmail())
                         .switchIfEmpty(ApplicationExceptions.invalidEmailFormat());
                        };
    }

    public static UnaryOperator<Mono<CustomerDto>> validate1(){
        return customerDto -> {
            return customerDto
                    .filter(hasName())
                    .switchIfEmpty(ApplicationExceptions.missingName())
                    .filter(hasEmail())
                    .switchIfEmpty(ApplicationExceptions.missingEmail())
                    .filter(hasValidEmail())
                    .switchIfEmpty(ApplicationExceptions.invalidEmailFormat());
        };
    }
    // IN prod , we remove the info logging  &  use debug level logging
    // To Do - Add more validations like restricted characters etc
    private static Predicate<CustomerDto> hasName() {
        return dto -> {
            log.debug("debug if name is not null  dto = {}", dto);
            log.info("Validating if name is not null dto: {}", dto);
            return Objects.nonNull(dto.name());
        };
    }
// To Do - Add more validations like restricted characters etc
    private static Predicate<CustomerDto> hasEmail() {
        return dto -> {
            log.debug("debug if email is not null , dto = {}", dto);
            log.info("check if email is not null ,  dto: {}", dto);
            return Objects.nonNull(dto.email());
        };
    }

    private static Predicate<CustomerDto> hasValidEmail(){
        return dto ->{
            log.debug("check if email is valid , dto = {}", dto);
            log.info("check if email is valid {}" ,dto);
            return validateEmail(dto);
        };
    }

    // To Do - If the email contains social domains like gmail, yahoo
    private static boolean validateEmail(CustomerDto dto) {
        return dto.email().contains("@");
    }
}
