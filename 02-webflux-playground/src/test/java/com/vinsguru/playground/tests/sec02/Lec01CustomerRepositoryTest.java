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


    /*
    Understanding to test the Publisher

      To test the Publisher, we use StepVerifier

            [A] var Flux<Customer>  customers =  ................

            [B] StepVerifier::create(customers) , we use the as operator on the Publisher, to create the StepVerifier

            Next() // THis is to check on the Publisher's item
                   (a) expectNext(...)
                   (b) expectNextCount(...)
                   (c) assertNext(...)

      // What are we expecting , error or complete
          Complete/Error()
                 (a) expectComplete()  -> This is to check if the Publisher completes
                 (b) expectError()  -> This is to check if the Publisher errors out

     //  Verify (This is what subscribes and runs the test)
             verify()

     */
    @Test
    public void findAll() {
        // Get Flux<Customer>
        this.repository.findAll()
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
    public void findByName() {
        this.repository.findByName("jake1")
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
        this.repository.save(customer)  // Returns the inserted Customer i.e Mono<Customer>
                       .doOnNext(c -> log.info(" Customer created is -> {}", c))
                       .as(StepVerifier::create)
                     // Test[1] = Assert that The customerID should not be null
                       .assertNext(c -> Assertions.assertNotNull(c.getId()))
                       .expectComplete()
                       .verify();

        //  Test[2] = Assert on the increase in count
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

 this.repository.findByName("xxx")
                .doOnNext(c -> c.setName("YYY"));


 var customer = repository.findByName("xxx")
 customer.setName("yyy"));


Traditional (Imperative) Mutation

(a)Synchronous Execution: The method findByName("xxx") returns an entity directly (e.g., Customer object).

(b)Immediate Mutation: The mutation happens immediately after fetching the entity.

(c)Blocking: The thread is blocked while retrieving the entity from the database.

(d)Direct Object Manipulation: The Customer object is modified directly after retrieval.


Reactive Mutation (Using Project Reactor)

(a)Asynchronous & Non-Blocking: findByName("xxx") likely returns a Mono<Customer> (from Project Reactor), meaning it is
executed asynchronously and the thread is  not blocked.
------------------------------------------------------------------------------------------------------
So when we  say findByName or findByID we will not know when we get the result  because it's  IO .
So when we get the customer that  time, the doOnNext()  will be setting the name like this.
------------------------------------------------------------------------------------------------------
(b)Mutation Happens Within the Reactive Pipeline: doOnNext(c -> c.setName("YYY")) modifies the entity when it is emitted,
but it does not return a new object.

(c)No Execution Without Subscription: This pipeline alone does not execute unless it is subscribed to
(e.g., via .subscribe() or .block()).

(d)Declarative Approach: Instead of directly mutating an object, you declare what should happen when the data is available.


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
