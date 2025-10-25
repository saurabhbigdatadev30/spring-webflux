package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.mapper.EntityDtoMapper;
import com.vinsguru.playground.sec03.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll()
                                      .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> fetchAllCustomers() {
        return this.customerRepository.fectchAllCustomers()
                .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // zero-indexed
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id) // Returns the Mono<Customer>
                                      // Mono<Customer> Transform to Mono<CustomerDto>
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity) // we should do input validation. let's worry about it later!
                   .flatMap(this.customerRepository::save)
                   .map(EntityDtoMapper::toDto);
    }

/*
 1. map wraps the returned Mono and gives you a Mono<Mono<Customer>>.
 2. The inner Mono is not subscribed, so the save does not execute.
 3. flatMap flattens the inner Mono i.e Mono<Mono<Customer>> => into Mono<Customer> and subscribes to it as part of the chain
    the save actually runs.
 */
   public void saveCustomerTest(Mono<CustomerDto> mono) {
      var result =   mono.map(EntityDtoMapper::toEntity)
                       // Since we are using map, we get Mono<Mono
                        .map(this.customerRepository::save)
                        .subscribe();

   }
   /*
    1. First we try to find the customer by ID. If found, we proceed to update.
      a. When we execute customerRepository.findById(customerID)  & only if customer with given ID exists .... the stream is
         proceeded further.
      b. If customerRepository.findById(customerID) not found ..  then Spring Data returns Mono.empty() , without executing
          the rest of the chain.
    */
    public Mono<CustomerDto> updateCustomer(Integer customerID, Mono<CustomerDto> customerDto) {
        return this.customerRepository.findById(customerID)
                                      .flatMap(entity -> customerDto)
                                      .map(EntityDtoMapper::toEntity)
                                      .doOnNext(c -> c.setId(customerID)) // this is safe
                                      .flatMap(this.customerRepository::save)
                                      .map(EntityDtoMapper::toDto);
    }



    public Mono<Boolean> deleteCustomerById(Integer id){
        return this.customerRepository.deleteCustomerById(id);
    }

}

