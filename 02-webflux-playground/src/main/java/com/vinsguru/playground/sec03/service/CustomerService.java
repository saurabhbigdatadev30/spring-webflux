package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.mapper.EntityDtoMapper;
import com.vinsguru.playground.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll() // returns Flux<Customer>
                //.map(c -> EntityDtoMapper.toDto(c));
                  .map(EntityDtoMapper::toDto);
    }

   // Using native query to fetch all customers
    public Flux<CustomerDto> fetchAllCustomers() {
        return this.customerRepository.fectchAllCustomers() // returns Flux<Customer>
                .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        log.info("Get all customers - page: {}, size: {}", page, size);
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // zero-indexed
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id) // Returns the Mono<Customer>
                                      .map(EntityDtoMapper::toDto);
    }


   public void saveCustomerUsingMap(Mono<CustomerDto> customerDto){
    customerDto.map(EntityDtoMapper::toEntity)
            .map(c -> this.customerRepository.save(c))
            .subscribe();
   }


    // We use flatMap here because the mapper returns a Publisher (Mono<Customer>)
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer customerID, Mono<CustomerDto> customerDto) {
        return this.customerRepository.findById(customerID)
                                      .flatMap(entity -> customerDto)
                                      .map(EntityDtoMapper::toEntity)
                                      .doOnNext(c -> c.setId(customerID)) // this is safe
                                      .flatMap(this.customerRepository::save)
                                      .map(EntityDtoMapper::toDto);
    }

    /*
     Usage of map vs flatMap
       1.  Use map when the mapper returns a simple value i.e not a Publisher e.g converting Entity to Dto or Dto to Entity
       2.  Use flatMap when the mapper returns a Publisher i.e repository calls like save(...)
            In this case mapper (entity -> customerDto) returns Mono<CustomerDto> which is a Publisher.
             Wrapping it with map will yield Mono<Mono<CustomerDto>> which is not desired.
     */
    public Disposable updateCustomer1(Integer customerID, Mono<CustomerDto> customerDto) {
        return this.customerRepository.findById(customerID)
                .map(entity -> customerDto)
                .subscribe();
    }

    public Mono<Boolean> deleteCustomerById(Integer id){
        return this.customerRepository.deleteCustomerById(id);
    }

}

