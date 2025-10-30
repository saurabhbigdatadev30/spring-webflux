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
                .delayElements(Duration.ofMillis(9000)) // Simulate processing delay
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

        //  KEY POINT: Thread is NOT blocked - code continues immediately
        methodX();

        // Simulate other work while query executes in background
         performOtherOperations1();

        log.info("Main method continues without waiting for DB query!");
        log.info("End of main execution {}  " , LocalDateTime.now());
    }

    private void performOtherOperations1() {
        log.info("performOtherOperations1() is executing in Thread:   {}", Thread.currentThread().getName());

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

    public void demonstrateConcurrentQueries() {
        System.out.println("\n--- CONCURRENT QUERIES DEMO ---");

        AtomicInteger completedQueries = new AtomicInteger(0);

        Flux<Customer> keCustomers = customerRepository.findByEmailEndingWithQuery("ke@gmail.com")
                .doOnSubscribe(sub -> System.out.println("Starting ke@gmail.com search on thread " + Thread.currentThread().getName() + "..."))
                .delayElements(Duration.ofMillis(2000))
                .doOnComplete(() -> {

                    int completed = completedQueries.incrementAndGet();
                    System.out.println(" ke@gmail.com query search completed (" + completed + "/3)");
                });

        Flux<Customer> exampleCustomer = customerRepository.findByEmailEndingWithQuery("@example.com")
                .doOnSubscribe(sub -> System.out.println(" Starting @example.com search..." + Thread.currentThread().getName() + "..."))
                .delayElements(Duration.ofMillis(2000))
                .doOnComplete(() -> {

                    int completed = completedQueries.incrementAndGet();
                    System.out.println("@example.com completed (" + completed + "/3)");
                });


        Flux<Customer> getAllCustomers = customerRepository.findAll()
                .doOnSubscribe(sub -> System.out.println("searching all customer..." + Thread.currentThread().getName() + "..."))
                .delayElements(Duration.ofMillis(500))
                .doOnComplete(() -> {

                    int completed = completedQueries.incrementAndGet();
                    System.out.println(" Search All  query completed (" + completed + "/3)");
                });

        // Subscribe to all queries - they run concurrently!
        keCustomers.subscribe();
        exampleCustomer.subscribe();
        getAllCustomers.subscribe();

        System.out.println(" All three queries started concurrently!");
        System.out.println("Main thread complete immediately without blocking  !" + Thread.currentThread().getName() );
    }


}
