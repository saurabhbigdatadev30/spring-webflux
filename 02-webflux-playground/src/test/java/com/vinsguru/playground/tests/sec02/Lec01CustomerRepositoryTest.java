package com.vinsguru.playground.tests.sec02;

import com.vinsguru.playground.sec02.entity.Customer;
import com.vinsguru.playground.sec02.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
@Slf4j
public class Lec01CustomerRepositoryTest extends AbstractTest {
    // Using Lombok @Slf4j annotation to avoid boilerplate code
    // private static final Logger log = LoggerFactory.getLogger(Lec01CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository repository;

    @Test
    public void getAllCustomers() {
        this.repository.fectchAllCustomers()
                .doOnNext(c -> log.info("Customers returned =>  {} ", c))
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
    public void getByCustomerId() {
        this.repository.findById(2)
                .as(dto -> StepVerifier.create(dto))
                .assertNext(c -> Assertions.assertEquals("mike", c.getName()))
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
        // customer id is the PK & is auto incremental by the DataBase, so this will be set automatically
        customer.setName("marshal1");
        customer.setEmail("marshal1@gmail.com");
        this.repository.save(customer)  // returns the inserted Customer i.e Mono<Customer>
                .doOnNext(c -> log.info(" Customer inserted is -> {}", c))
                .as(StepVerifier::create)
                // Test[1] = Assert that The customerID should not be null
                .assertNext(c -> Assertions.assertNotNull(c.getId()))
                .expectComplete()
                .verify();

        this.repository.deleteById(11)
              /*
               then() is invoked on the Mono returned by this.repository.deleteById(11) (a Mono<Void>).
               It waits for that completion and then switches to the provided Mono (this.repository.count()
               returns Mono<Long>).
               */
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }

// Refactored insertAndDeleteCustomer() as chain the operations rather than having separate StepVerifiers
    @Test
    public void insertAndDeleteCustomerUpdatedRefactordUsingMap() {
        var customer = new Customer();
        customer.setName("marshal1");
        customer.setEmail("marshal1@gmail.com");
        this.repository.save(customer)
                .doOnNext(c -> log.info("Customer inserted -> {}", c))
                .map(this.repository::delete)
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }

    @Test
    public void insertAndDeleteCustomerUpdatedRefactord() {
        var customer = new Customer();
        customer.setName("marshal1");
        customer.setEmail("marshal1@gmail.com");
        this.repository.save(customer)
                .doOnNext(c -> log.info("Customer inserted -> {}", c))
                .flatMap(this.repository::delete)
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }

    @Test
    public void updateCustomerUsingMap() {
        this.repository.findByName("ethan")
                // Flux<Customer>.map(Function<Customer , Mono<Customer>>) // returns = Flux<Mono<Customer>>
                .doOnNext(c -> c.setName("noel123"))
                .map(this.repository::save)
                .as(StepVerifier::create);
    }

    /*
      the flatMap is Flux<T>.flatMap( where T = customer &  R = Customer) . So the output is Flux<Customer>
     */
    @Test
    public void updateCustomerUsingflatMap() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel123"))
              //  .flatMap(c -> this.repository.save(c))
                .flatMap(this.repository::save) // returns Flux<Customer>
                .as(StepVerifier::create);
    }

    // Using flatMap to update the customer
    @Test
    public void updateCustomer() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel12"))
                // flatMap is required whenever the mapper returns a Mono/Flux.
                .flatMap(this.repository::save)
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c ->
                {
                    Assertions.assertNotNull(c.getId());
                    Assertions.assertEquals("noel12", c.getName());
                })
                .expectComplete()
                .verify();
    }






}

