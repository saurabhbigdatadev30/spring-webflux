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

    /*
         Understanding the m() -- mapper function input & output types
         Input (per element):  Customer(entity)
         Output (per element): CustomerDto
         Resulting publisher: Flux<CustomerDto> ...  Simple value not Publisher
   */
    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll() // returns Flux<Customer>
                                      .map(EntityDtoMapper::toDto);
    }

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

/*
  1. The mapper here (this.customerRepository::save) returns Mono<Customer> i.e a Publisher.
  2. Using the map with this mapper yields nested  Mono<Mono<Customer>>
 */
   public void saveCustomerTest(Mono<CustomerDto> mono) {
        mono.map(EntityDtoMapper::toEntity)
                        .map(this.customerRepository::save)
                        .subscribe();

   }
    // We use flatMap here because the mapper returns a Publisher (Mono<Customer>)
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
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

