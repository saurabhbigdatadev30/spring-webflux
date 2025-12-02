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
        customer.setName("marshal");
        customer.setEmail("marshal@gmail.com");
        this.repository.save(customer)  // returns the inserted Customer i.e Mono<Customer>
                .doOnNext(c -> log.info(" Customer inserted is -> {}", c))
                .as(StepVerifier::create)
                // Test[1] = Assert that The customerID should not be null
                .assertNext(c -> Assertions.assertNotNull(c.getId()))
                .expectComplete()
                .verify();


        this.repository.deleteById(11)
                // After deletion , fetch the count of total customers.
                .then(this.repository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }



    /*@Test
    public void insertAndDeleteCustomer() {
        // insert new customer
           var customer = new Customer();
           customer.setName("marshal");
           customer.setEmail("marshal@gmail.com");

        this.repository.save(customer) // returns Mono<Customer>
                .doOnNext(c -> log.info("Customer inserted -> {}", c))
                .flatMap(savedCustomer ->
                        // delete using custom deleteCustomerById and then verify count
                        this.repository.deleteById(savedCustomer.getId())
                                .doOnNext(deleted -> Assertions.assertTrue(deleted))
                                .then(this.repository.count())
                )
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }*/

    /*
    + Need of using FlatMap
    + Step-by-step Understanding of the updateCustomerUsingMap:

      this.repository.findByName("ethan")
            - Return type is Flux<Customer>.
            - So the upstream to map is Flux<Customer>.

	   map signature (simplified):
       Flux<T>.map(Function<? super T, ? extends R> mapper)
             - It transforms each element (Customer) emitted by the Flux,
             - It does not transform the Flux itself.
             - So the mapper receives Customer, not Flux<Customer>.

	   The mapper:
         c -> this.repository.save(c)
             - this.repository.save(c) returns Mono<Customer>.
             - Therefore map turns Flux<Customer> into Flux<Mono<Customer>>.


   To flatten and execute the inner Mono<Customer> publishers, you need flatMap:
       - flatMap has signature:
       Flux<T>.flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper)
       - It takes each Customer, calls save (returning Mono<Customer>), and flattens them into a single Flux<Customer>,
       subscribing to each inner Mono.

     */

    @Test
    public void updateCustomerUsingMap() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel123"))
                .map(this.repository::save)
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
                .assertNext(c -> Assertions.assertEquals("noel12", c.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    public void updateCustomerUsingflatMap() {
        this.repository.findByName("ethan")
                .doOnNext(c -> c.setName("noel123"))
                .flatMap(this.repository::save) // returns Flux<Customer>
                .as(StepVerifier::create);
    }




}

