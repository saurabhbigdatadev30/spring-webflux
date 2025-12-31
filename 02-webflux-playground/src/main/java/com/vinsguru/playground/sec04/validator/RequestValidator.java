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

    private static final Predicate<String> NAME_ALLOWED_CHARS =
            (name) -> {
                           log.info("name check for allowed characters, name={}", name);
                           return name != null && name.matches("[\\p{L} .'-]+");
            };

    public static UnaryOperator<Mono<CustomerDto>> validate() {
        return customerDto ->
        {
           return  customerDto
                   .doOnNext(dto -> log.info("Validate the incoming request dto: = {}", dto))
                   .filter(dto->hasName().test(dto))
                   .filter(hasName())
                   .switchIfEmpty(ApplicationExceptions.missingName())
                   .filter(hasNameWithoutSpecialChars())
                   .switchIfEmpty(ApplicationExceptions.invalidNameFormat())
                   .filter(hasEmail())
                   .switchIfEmpty(ApplicationExceptions.missingEmail())
                   .filter(isEmailValid())
                    .switchIfEmpty(ApplicationExceptions.invalidEmailFormat());
                        };
    }

 // Define custom validation using Predicates ------------------------
    private static Predicate<CustomerDto> hasName() {
        return dto -> {
            log.info("Validating if name is not null dto: {}", dto);
            return dto!=null && Objects.nonNull(dto.name());
        };
    }

    private static Predicate<CustomerDto> hasNameWithoutSpecialChars() {
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

    public static Predicate<CustomerDto> isEmailValidByLambda() {
        return dto -> {
            return isHostNameValid(dto);
        };
    }

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

