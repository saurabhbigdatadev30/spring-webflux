package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.mapper.EntityDtoMapper;
import com.vinsguru.playground.sec03.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
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
        return this.customerRepository.fectchAllCustomers() // returns Flux<Customer>
                .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // zero-indexed
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id) // Returns the Mono<Customer>
                                      .map(EntityDtoMapper::toDto);
    }
// Takes Publisher as input parameter and returns Publisher as output
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)  // Mono<CustomerDto>  -> map() ->  Mono<Customer>
                   .flatMap(this.customerRepository::save)
                   .map(EntityDtoMapper::toDto);
    }

/*

 */
   public void saveCustomerTest(Mono<CustomerDto> mono) {
        mono.map(EntityDtoMapper::toEntity)
                        .map(this.customerRepository::save)
                        .subscribe();

   }
   /*
      Whatever we have in the table fetched using findById(customerID) will be replaced by the incoming customerDto.
        1. We first fetch the existing entity using findById(customerID)
        2. Replace the existing entity with the incoming customerDto
        3. Save the updated entity back to the database .
        4. Return the updated entity as CustomerDto.
        Note: If the customerID does not exist in the DB, the Mono will be empty & the rest of the chain will be skipped.
    */
    public Mono<CustomerDto> updateCustomer(Integer customerID, Mono<CustomerDto> customerDto) {
        return this.customerRepository.findById(customerID)
                                      .flatMap(entity -> customerDto)
                                      .map(EntityDtoMapper::toEntity)
                                      .doOnNext(c -> c.setId(customerID)) // this is safe
                                      .flatMap(this.customerRepository::save)
                                      .map(EntityDtoMapper::toDto);
    }


    /*
    Why we use flatMap  instead of map in the above while replacing the entity with the request DTO?
    Use flatMap when your mapper returns a Publisher (e.g., Mono/Flux),
    including repository calls like save(...)

    If the mapper returns a Publisher (Mono or Flux), you need flatMap so the inner publisher is subscribed
    and its emissions are propagated.
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

