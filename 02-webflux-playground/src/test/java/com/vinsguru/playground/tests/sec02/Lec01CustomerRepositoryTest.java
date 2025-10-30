package com.vinsguru.playground.tests.sec02;

import com.vinsguru.playground.sec02.entity.Customer;
import com.vinsguru.playground.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class Lec01CustomerRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository repository;

    @Test
    public void getAllCustomers()
    {
        this.repository.findAll()
                .doOnNext(c -> log.info("Customers =  {} ", c ))
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



    @Test
    public void findByEmailEndingWith() {
        //this.repository.findByEmailEndingWith("ke@gmail.com")
        // Using native Query instead of JPA
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

       /*   Test[2] = Assert on the increase in count of total number of customers
        this.repository.count()
                .as(StepVerifier::create)
                .expectNext(11L)
                .expectComplete()
                .verify();
*/

        // Test[3] =  For the  delete assert on the count reduction
        this.repository.deleteById(11)
                // After deletion , fetch the count of total customers.
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


# Update Operation in non blocking way using doOnNext()
 this.repository.findByName("xxx")
                .doOnNext(c -> c.setName("YYY"));

Why are using the doOnNext() operator is mainly because it's non-blocking IO. When we invoke findByID() ,we will not know when
we get the result. The doOnNext() will be executed  when we get the customer, so it will now be mutating the customerName
without blocking the main thread.
 */


    @Test
    public void updateCustomer() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel12"))
                // Here we should use flatMap instead of map because repository.save returns Mono<Customer>
                //.flatMap(c -> this.repository.save(c))
                .flatMap(this.repository::save)
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("noel12", c.getName()))
                .expectComplete()
                .verify();
    }

    /*
    When we use .map(this.repository::save), each element is mapped to a Mono<Customer>, so the resulting type is
    Flux<Mono<Customer>>.
    This is a nested publisher, not a flat stream of Customer.
                      .map(this.repository::save) → Flux<Mono<Customer>>
                      .flatMap(this.repository::save) → Flux<Customer>

       The type is not always obvious in the code unless you inspect it or use an operator like .as(StepVerifier::create),
       which expects a Publisher<T>. If you want a flat stream, use .flatMap instead of .map.
     */
    @Test
    public void updateCustomerUsingMap() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel123"))
                .map(this.repository::save)
                .as(StepVerifier::create);
    }
}

