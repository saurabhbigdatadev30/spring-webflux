package com.vinsguru.playground.sec02.repository;

import com.vinsguru.playground.sec02.entity.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

     Flux<Customer> findByName(String name);

    @Query(value = "SELECT * FROM customer WHERE name = :name")
    Flux<Customer> findByNameQuery(String name);

    Flux<Customer> findByEmailEndingWith(String email);

    // Using the native SQL Query for fetching all customers , instead of JPA query methods
    @Query(value = "SELECT * FROM customer")
    Flux<Customer> fectchAllCustomers();


// Multi-line Query from java 15 onwards
  @Query("""
            SELECT customer.id,
                   customer.name,
                   customer.email
            FROM customer
            WHERE customer.email LIKE CONCAT('%', ?1)
         """)
  Flux<Customer> findByEmailEndingWithQuery(String email);

}
