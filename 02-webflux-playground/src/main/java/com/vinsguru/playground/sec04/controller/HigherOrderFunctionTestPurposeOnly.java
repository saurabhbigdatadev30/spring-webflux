package com.vinsguru.playground.sec04.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
public class HigherOrderFunctionTestPurposeOnly {

    private static final Set<String> BLOCKED_HOST_NAMES = Set.of("gmail.com", "yahoo.com" , "hotmail.com");

    public static Predicate<String> hasAtSymbol() {
        // The method returns a Lambda Expression that implements the Predicate functional interface
        return email -> {
            log.info("Checking for @ symbol in email: {}", email);
            return email != null && email.contains("@");
        };
    }

    // Higher Order Function
    public static Predicate<String> hasValidHostName(Set<String> blockedHostNames) {
        return email -> {
            log.info("Checking valid domain name = : {} in email", email);
            if (email == null) return false;
            int at = email.indexOf('@');
            if (at < 0 || at == email.length() - 1) return false;
            String domain = email.substring(at + 1).toLowerCase();
            return !blockedHostNames.contains(domain);
        };
    }
   // Predicate Composition - Higher Order Predicate , we join Predicates together with and .
    public static Predicate<String> isCorporateEmailValid() {
        return hasAtSymbol().and(hasValidHostName(BLOCKED_HOST_NAMES));
    }

    private static void higherOrderPredicateDemo() {
        System.out.println(isCorporateEmailValid().test("a@company.com")); // true
        System.out.println(isCorporateEmailValid().test("a@gmail.com"));   // false
        System.out.println(isCorporateEmailValid().test("a@hotmail.com"));  // false
    }

    public static void main(String[] args) {
        higherOrderPredicateDemo();
        //higherOrderFunctionDemo();
    }



    private static void higherOrderFunctionDemo() {
        Function<Integer, Integer> square = (x) -> {
            return x * x;
        };
        Function<Integer, Integer> loggedSquare = withLogging("square", square);
        System.out.println(loggedSquare.apply(5));
    }
    public static <T, R> Function<T, R> withLogging(String label, Function<T, R> fn) {
        return t -> {
            log.info("{} input={}", label, t);
            R r = fn.apply(t);
            log.info("{} output={}", label, r);
            return r;
        };
    }
}
