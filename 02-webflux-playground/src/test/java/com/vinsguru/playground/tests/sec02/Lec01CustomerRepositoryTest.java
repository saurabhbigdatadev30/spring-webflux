package com.vinsguru.playground.tests.sec02;

import com.vinsguru.playground.sec02.entity.Customer;
import com.vinsguru.playground.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Objects;

public class Lec01CustomerRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository repository;

    @Test
    public void findAll() {
        // Get Flux<Customer> from the repository using the native query
        this.repository.fectchAllCustomers()
                       .doOnNext(c -> log.info("Get the Customer from Publisher ===>>> {}", c))
                       .as(StepVerifier::create)
                       .expectNextCount(10)
                       .expectComplete()
                       .verify();
    }

    @Test
    public void findById() {
        this.repository.findById(3)
                       .doOnNext(c -> log.info("{}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("jake", c.getName()))
                       .expectComplete()
                       .verify();
    }

    @Test
    public void findByNameUsingQuery() {
        this.repository.findByNameQuery("jake")
                       .doOnNext(c -> log.info("{}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                       .expectComplete()
                       .verify();
    }
// Java.lang.AssertionError: expectation "assertNext" failed (expected: onNext(); actual: onComplete())


    /*
        Query methods
        https://docs.spring.io/spring-data/relational/reference/r2dbc/query-methods.html

        Task: find all customers whose email ending with "ke@gmail.com"
    */

    @Test
    public void findByEmailEndingWith() {
        //this.repository.findByEmailEndingWith("ke@gmail.com")
        // Using native Query
        this.repository.findByEmailEndingWithQuery("ke@gmail.com")
                       .doOnNext(c -> log.info("Getting the Customers with mail ke@gmail.com = {}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("mike@gmail.com", c.getEmail()))
                       .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                       .expectComplete()
                       .verify();
    }

    @Test
    public void insertAndDeleteCustomer() {
        // insert new customer
        var customer = new Customer();
        // customer id is the PK & is auto incremental , so this will be set automatically
        customer.setName("marshal");
        customer.setEmail("marshal@gmail.com");
        this.repository.save(customer)  // returns the inserted Customer i.e Mono<Customer>
                       .doOnNext(c -> log.info(" Customer created is -> {}", c))
                       .as(StepVerifier::create)
                     // Test[1] = Assert that The customerID should not be null
                       .assertNext(c -> Assertions.assertNotNull(c.getId()))
                       .expectComplete()
                       .verify();

        //  Test[2] = Assert on the increase in count of total number of customers
        this.repository.count()
                       .as(StepVerifier::create)
                       .expectNext(11L)
                       .expectComplete()
                       .verify();


        // Test[3] = Email to be verified for the inserted record
        this.repository.findByName("marshal")
                .doOnNext(c -> log.info(" Customer details ----> {}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("marshal@gmail.com", c.getEmail()))
                .expectComplete()
                .verify();

        // Test[4] =  For the  delete assert on the count reduction
        this.repository.deleteById(11)
                       .then(this.repository.count())
                       .as(StepVerifier::create)
                       .expectNext(10L)
                       .expectComplete()
                       .verify();
    }

/*
What is the difference between the traditional style to mutating the entity and the reactive sytle of mutating in
the below Java 17 style of  code

 var customer = repository.findByName("xxx")
 customer.setName("yyy"));


# Update Operation in non blocking way
 this.repository.findByName("xxx")
                .doOnNext(c -> c.setName("YYY"));


Why  are using the doOnNext() operator is mainly because it's non-blocking IO. When we invoke findByID() ,we will not know when we get the result.
The doOnNext operator will be executed  when we get the customer, so it will now be mutating the customerName without blocking

 */


    @Test
    public void updateCustomer() {
        this.repository.findByName("ethan")
                       .filter(Objects::nonNull)
                       .doOnNext(c -> c.setName("noel12"))
                        //.flatMap(c -> this.repository.save(c))
                       .flatMap(this.repository::save)
                       .doOnNext(c -> log.info("{}", c))
                       .as(StepVerifier::create)
                       .assertNext(c -> Assertions.assertEquals("noel12", c.getName()))
                       .expectComplete()
                       .verify();
    }

}
