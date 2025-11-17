package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.entity.Customer;
import com.vinsguru.playground.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class R2DBCAsynchService {
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Demonstrates R2DBC's Non-blocking and Asynchronous behavior
     */
    public void demonstrateAsyncNonBlockingExecution() {
     log.info("executing demonstrateAsyncNonBlockingExecution");

        // Start the asynchronous query
        Flux<Customer> customerFlux = customerRepository.findByEmailEndingWithQuery("ke@gmail.com")
                .delayElements(Duration.ofMillis(9000)) // Simulate processing delay of the DB response in execution of findByEmailEndingWithQuery
                .doOnSubscribe(subscription ->
                              log.info("[findByEmailEndingWithQuery] Query started on thread: {} " , Thread.currentThread().getName()))
                .doOnNext(customer -> {
                    // Process each customer as it arrives
                    log.info("Fetch customer details from DB as callback in thread {} Customer Name =  {}  Customer email =({})",
                            Thread.currentThread().getName(),
                            customer.getName(),
                            customer.getEmail());
                })
                .doOnComplete(() ->
                        log.info("DB Query findByEmailEndingWithQuery] completed at: {} " , LocalDateTime.now()))
                .doOnError(error ->
                        log.error(" Error occurred {} " , error.getMessage()));

        // Subscribe to the Flux (this triggers the execution but doesn't block)
        customerFlux.subscribe();

        // Simulate other work while query executes in background
         performOtherOperations();

        log.info("Main method continues without waiting for DB query!");

    }

    private void performOtherOperations() {
        log.info("performOtherOperations() is executing in Thread:   {}", Thread.currentThread().getName());

        for (int i = 1; i <= 3; i++) {
            log.info("Operation {} completed at {}", i, LocalDateTime.now());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Operation interrupted", e);
            }
        }
        log.info("performOtherOperations1 is complete at = {}" , LocalDateTime.now());
        log.info("THis proves the thread is NOT blocked by the DB operation!!");
    }

    private void methodX() {
        log.info("methodX() is executing in Thread:   {}", Thread.currentThread().getName());
        log.info("methodX is complete at = {}" , LocalDateTime.now());
    }


    /**
     * Demonstrates multiple concurrent R2DBC queries executing asynchronously without blocking each other & also the main thread
     */
    public void demonstrateConcurrentQueries() {
        log.info("\n---  demonstrateConcurrentQueries started ---");

        AtomicInteger completedQueries = new AtomicInteger(0);

        Flux<Customer> keCustomers = customerRepository.findByEmailEndingWithQuery("ke@gmail.com")
                .doOnSubscribe(sub -> log.info("Starting Query[1] ke@gmail.com search in Thread = {} , @Time = {} " , Thread.currentThread().getName() , LocalDateTime.now()))
                .delayElements(Duration.ofMillis(3000))
                .doOnComplete(() -> {
                    int count = completedQueries.incrementAndGet();
                    log.info("Query[1] ke@gmail.com query count = {} completed by thread = {} , at time = {} ", count , Thread.currentThread().getName(), LocalDateTime.now());
                });

        Flux<Customer> exampleCustomer = customerRepository.findByEmailEndingWithQuery("@example.com")
                .doOnSubscribe(sub -> log.info("Starting Query[2] @example.com search in Thread = {} , @Time = {} ", Thread.currentThread().getName() , LocalDateTime.now()))
                .delayElements(Duration.ofMillis(2000))
                .doOnComplete(() -> {
                    int completed = completedQueries.incrementAndGet();
                    log.info("Query[2] @example.com count = {} completed by thread = {} , at time = {} ", completed , Thread.currentThread().getName(), LocalDateTime.now());
                });

        Flux<Customer> getAllCustomers = customerRepository.findAll()
                .doOnSubscribe(sub -> log.info("Starting Query[3] ... Searching all customer in Thread = {} , @Time = {} ", Thread.currentThread().getName() , LocalDateTime.now()))
                .delayElements(Duration.ofMillis(500))
                .doOnComplete(() -> {
                    int completed = completedQueries.incrementAndGet();
                    log.info("Query[3] @example.com count = {} completed by thread = {} , at time  = {} ", completed , Thread.currentThread().getName() , LocalDateTime.now());
                });

        // Subscribe to all the queries - they run concurrently!
        keCustomers.subscribe();
        exampleCustomer.subscribe();
        getAllCustomers.subscribe();

        log.info(" All the three queries started concurrently!");

    }


}
