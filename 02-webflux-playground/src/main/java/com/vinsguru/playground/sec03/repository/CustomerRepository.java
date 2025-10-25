package com.vinsguru.playground.sec03.repository;

import com.vinsguru.playground.sec03.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    @Modifying // for demo
    @Query("delete from customer where id=:id")
    Mono<Boolean> deleteCustomerById(Integer id);

    // pagination support , Spring Data will automatically generate the limit & offset based on the Pageable parameter
    /*

     */
    Flux<Customer> findBy(Pageable pageable);

    @Query(value = "SELECT * FROM customer")
    Flux<Customer> fectchAllCustomers();

    @Query(value = "SELECT customer.ID, customer.NAME, customer.EMAIL FROM customer WHERE customer.EMAIL LIKE CONCAT('%', ?1)")
    Flux<Customer> findByEmailEndingWithQuery(String email);


}
