package com.vinsguru.playground.sec02.repository;

import com.vinsguru.playground.sec02.entity.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Flux<Customer> findByName(String name);

    Flux<Customer> findByEmailEndingWith(String email);

    // Using the native SQL Query
    @Query(value = "SELECT customer.ID, customer.NAME, customer.EMAIL FROM customer WHERE customer.EMAIL LIKE CONCAT('%', ?1)")
    Flux<Customer> findByEmailEndingWithQuery(String email);

}
