package com.vinsguru.playground.sec04.controller;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.exceptions.ApplicationExceptions;
import com.vinsguru.playground.sec04.service.CustomerService;
import com.vinsguru.playground.sec04.validator.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Flux<CustomerDto> allCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("paginated")
    public Mono<List<CustomerDto>> allCustomers(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {
        return this.customerService.getAllCustomers(page, size)
                                   .collectList();
    }

    @GetMapping("{id}")
    public Mono<CustomerDto> getCustomer(@PathVariable Integer id) {
        return this.customerService.getCustomerById(id)
                                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }

/*
    1.Validate Publisher obj @RequestBody<Mono<CustomerDto>> customermono, before passing it to the service layer.
    2.mono.transform(..) takes Function where T = Mono<CustomerDto> and R = Mono<CustomerDto> (after validation).
    3.RequestValidator.validate() returns Function<Mono<CustomerDto>, Mono<CustomerDto>>
    4.Invocation of the validate() method will be validate().apply(customerDto)
 */
    @PostMapping
    public Mono<CustomerDto> saveCustomer(@RequestBody Mono<CustomerDto> customermono) {
        return customermono.transform(customerdto -> RequestValidator.validate().apply(customerdto))
    //    return customermono.transform(RequestValidator.validate())
                .doOnNext(customerDto -> log.info("validated CustomerDto: {}", customerDto))
              //   .flatMap(this.customerService::saveCustomer);
                   .as(this.customerService::saveCustomer);
    }



    @PutMapping("{id}")
    public Mono<CustomerDto> updateCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> mono) {
        return mono.transform(RequestValidator.validate())
                   .as(validReq -> this.customerService.updateCustomer(id, validReq))
                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }



    @DeleteMapping("{id}")
    public Mono<Void> deleteCustomer(@PathVariable Integer id) {
        return this.customerService.deleteCustomerById(id)
                                   .filter(b -> b)
                                   .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                                   .then();
    }

}
