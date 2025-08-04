package com.vinsguru.playground.sec04.validator;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.exceptions.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<CustomerDto>> validate() {
        return mono -> mono.filter(hasName())
                           .switchIfEmpty(ApplicationExceptions.missingName())
                           .filter(isEmailNotNull())
                           .switchIfEmpty(ApplicationExceptions.missingEmail())
                           .filter(hasValidEmailRefactored())
                           .switchIfEmpty(ApplicationExceptions.missingValidEmail());
    }

    private static Predicate<CustomerDto> hasName() {
        return dto -> dto != null && Objects.nonNull(dto.name());
    }

    private static Predicate<CustomerDto> hasValidEmail() {
        return dto -> dto != null && Objects.nonNull(dto.email()) && dto.email().contains("@");
    }

    private static Predicate<CustomerDto> hasValidEmailRefactored() {
        List<String> socialDomains = List.of(
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "aol.com", "icloud.com", "live.com", "msn.com", "protonmail.com"
        );

        return dto -> {
            if (dto == null || dto.email() == null || !dto.email().contains("@")) {
                return false;
            }

            String domain = dto.email().substring(dto.email().indexOf("@") + 1).toLowerCase();
            return !socialDomains.contains(domain);
        };
    }

public static Predicate<CustomerDto> isNameNotNull(){
        return dto -> Objects.nonNull(dto.name());
}

public static Predicate<CustomerDto>isEmailNotNull(){
        return dto -> Objects.nonNull(dto.email());
}

}
