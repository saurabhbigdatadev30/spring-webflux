package com.vinsguru.playground.sec04.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
public class HigherOrderFunctionTestPurposeOnly {

    private static final Set<String> BLOCKED = Set.of("gmail.com", "yahoo.com" , "hotmail.com");

    public static Predicate<String> hasAtSymbol() {
        return email -> {
            log.info("Checking for @ symbol in email: {}", email);
            return email != null && email.contains("@");
        };
    }

    // Higher Order Function
    public static Predicate<String> domainNotIn(Set<String> blocked) {
        return email -> {
            log.info("Checking domainNotIn in email: {}", email);
            if (email == null) return false;
            int at = email.indexOf('@');
            if (at < 0 || at == email.length() - 1) return false;
            String domain = email.substring(at + 1).toLowerCase();
            return !blocked.contains(domain);
        };
    }

    public static Predicate<String> corporateEmail() {
        return hasAtSymbol().and(domainNotIn(BLOCKED));
    }

    public static void main(String[] args) {
       // higherOrderPredicateDemo();
        higherOrderFunctionDemo();

    }

    private static void higherOrderFunctionDemo() {
        Function<Integer, Integer> square = (x) -> {
            return x * x;
        };
        Function<Integer, Integer> loggedSquare = withLogging("square", square);
        System.out.println(loggedSquare.apply(5));
    }

    private static void higherOrderPredicateDemo() {
        System.out.println(corporateEmail().test("a@company.com")); // true
        System.out.println(corporateEmail().test("a@gmail.com"));   // false
        System.out.println(corporateEmail().test("a@hotmail.com"));  // false
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
