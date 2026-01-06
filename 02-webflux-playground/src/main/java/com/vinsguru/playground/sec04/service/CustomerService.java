package com.vinsguru.playground.sec04.service;

import com.vinsguru.playground.sec04.dto.CustomerDto;
import com.vinsguru.playground.sec04.mapper.EntityDtoMapper;
import com.vinsguru.playground.sec04.repository.CustomerRepository;
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

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // zero-indexed
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id)
                                      .map(EntityDtoMapper::toDto);
    }

    /*
     1. mono.map(..) -> Mono<Customer> . This is the input to the flatMap
     2. flatMap(customerRepository::save) -> Mono<Customer>.flatMap(customer, Function<Customer, Mono<Customer>>)
     */
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity) // returns Mono<Customer>
                  // .flatMap(entity -> this.customerRepository.save(entity))
                   .flatMap(this.customerRepository::save) //replace lambda with method reference
                   .map(EntityDtoMapper::toDto);
    }

    /*

        1. findById(..) returns Mono<CustomerEntity>
        2. Mono<CustomerEntity>.map(Function<CustomerEntity, Mono<CustomerDto>>)
        3. So, the result is Mono<Mono<CustomerDto>>
        4. This is an issue , because the inner mono is never subscribed to.
     */
    public void updateCustomerUsingMapIssue(Integer customerId , Mono<CustomerDto> mono) {
        this.customerRepository.findById(customerId)
                .map(entity -> mono)
                .subscribe();
    }

    public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> mono) {
        return this.customerRepository.findById(id)
                                      .flatMap(entity -> mono)
                                      .map(EntityDtoMapper::toEntity)
                                      .doOnNext(c -> c.setId(id)) // this is safe
                                      .flatMap(this.customerRepository::save)
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<Boolean> deleteCustomerById(Integer id){
        return this.customerRepository.deleteCustomerById(id);
    }

}

