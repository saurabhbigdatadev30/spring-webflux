package com.vinsguru.playground.sec03.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.service.CustomerService;
import com.vinsguru.playground.sec03.service.R2DBCAsynchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    R2DBCAsynchService asynchDemo;

    @GetMapping
    public Flux<CustomerDto> allCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("/fetchAllCustomersX")
    public Flux<CustomerDto> fetchAllCustomers() {
        return this.customerService.fetchAllCustomers();
    }

    @GetMapping("/async-execution")
    public Mono<String> demonstrateAsyncExecution() {
        log.info("HTTP Request received at {} " , LocalDateTime.now());

        // Start the demonstration
        asynchDemo.demonstrateAsyncNonBlockingExecution();

        // Return immediately without blocking
        return Mono.just("Async demonstration started! Check console for execution details. Time: " + LocalDateTime.now());
    }

    @GetMapping("/concurrent-queries")
    public Mono<String> demonstrateConcurrentQueries() {
        asynchDemo.demonstrateConcurrentQueries();
        methodX();
        return Mono.just(" Concurrent queries started! Check console for details.");
    }

    private void methodX() {
        log.info("methodX() is executing in Thread:   {}", Thread.currentThread().getName());
        log.info("Time: {}", LocalDateTime.now());
        log.info("methodX is complete at = {}" , LocalDateTime.now());
    }

    /*
      If we have million of records in the customer table, returning all the records in a single API call is not a good idea.
      Instead we should implement pagination.
     */
    // http://localhost:8081/customers/paginated?page=1&size=3"
    @GetMapping("paginated")
    public Flux<CustomerDto> allCustomers(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "3") Integer size) {
        return this.customerService.getAllCustomers(page, size);

    }
    // http://localhost:8081/customers/2
    @GetMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> getCustomer(@PathVariable Integer id) {
        return this.customerService.getCustomerById(id)
                //   .map(dto -> ResponseEntity.ok(dto))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<CustomerDto> saveCustomer(@RequestBody Mono<CustomerDto> mono) {
        return this.customerService.saveCustomer(mono);
        // To return response entity with status code
    }

    @PostMapping("persistCustomer")
    public Mono<ResponseEntity<CustomerDto>> saveCustomerUpdated(@RequestBody Mono<CustomerDto> mono)
    {
        return this.customerService.saveCustomer(mono)
                //   .map(dto -> ResponseEntity.ok(dto))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }


    @PutMapping("{id}")
    public Mono<ResponseEntity<CustomerDto>> updateCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> mono) {
        return this.customerService.updateCustomer(id, mono)
                //   .map(dto -> ResponseEntity.ok(dto))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {
        return this.customerService.deleteCustomerById(id)
                // Using filter we check that the this.customerService.deleteCustomerById(id) is true
                .filter(b -> b)  // If true then proceed to map else returns defaultIfEmpty
                .map(b -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());



    }

}
