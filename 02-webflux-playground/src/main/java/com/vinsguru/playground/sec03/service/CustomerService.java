package com.vinsguru.playground.sec03.service;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.mapper.EntityDtoMapper;
import com.vinsguru.playground.sec03.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return this.customerRepository.findAll() // returns Flux<Customer>
                 // .map(c -> EntityDtoMapper.toDto(c));
                    .map(EntityDtoMapper::toDto);
    }

   // Using native query to fetch all customers
    public Flux<CustomerDto> fetchAllCustomers() {
        return this.customerRepository.fectchAllCustomers() // returns Flux<Customer>
                //.map(c -> EntityDtoMapper.toDto(c))
                .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> getAllCustomers(Integer page, Integer size) {
        log.info("Get all customers - page: {}, size: {}", page, size);
        return this.customerRepository.findBy(PageRequest.of(page - 1, size)) // zero-indexed
                                      .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.customerRepository.findById(id) // Returns the Mono<Customer>
                //.map(c -> EntityDtoMapper.toDto(c))  using lambda
                 .map(EntityDtoMapper::toDto);   // The above line can be replaced with method reference
    }


    /*
      Understanding why using map here is incorrect
        1. Upstream to the map = Mono<CustomerDto>.map(...)
        2. Mapper = Function<Customer, Mono<Customer>>
        3 So, T = CustomerDto, R = Mono<Mono<Customer>>
        4. So, the return type of map will be Mono<Mono<Customer>>
        5. Since the inner Mono<Customer> is not subscribed to, the code inside the inner Mono will never execute.
            To fix this, we need to use flatMap instead. The flatMap will subscribe to the inner Mono<Customer>

     */
   public void saveCustomerUsingMap(Mono<CustomerDto> customerDto){
    customerDto.map(EntityDtoMapper::toEntity)
           // .map(c-> EntityDtoMapper.toEntity(c))
            .map(c -> this.customerRepository.save(c))
            // .map(c -> this.customerRepository.save(c)) returns Mono<Mono<Customer>> , Mono<Customer>.map(...)
            .subscribe();
   }


    // We use flatMap here because the mapper returns a Publisher (Mono<Customer>)
    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> mono) {
        return mono.map(EntityDtoMapper::toEntity)
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer customerID , Mono<CustomerDto> customerDto){
        return this.customerRepository.findById(customerID)
                .flatMap(customer -> customerDto)
               // .map(dto -> EntityDtoMapper.toEntity(dto))
                  .map(EntityDtoMapper::toEntity)
                .doOnNext(c -> c.setId(customerID))
                .flatMap(this.customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }


    /**
    1. this.customerRepository.findById(customerID) returns Mono<Customer>. So, Upstream to the map = Mono<Customer>.map(.....)
    2. Mapper = Function<Customer, Mono<CustomerDto>>
    3  So, T = Customer, R = Mono<CustomerDto>
    4. So, the return type of map = Mono<Mono<CustomerDto>>
    5. Since the inner Mono<CustomerDto> is not subscribed to, the code inside the inner Mono will never execute.
    6. Hence, this method will not update the customer record in the database.
    7. To fix this, we need to use flatMap instead
     */

    public void updateCustomerUsingMap(Integer customerID, Mono<CustomerDto> customerDto) {
         this.customerRepository.findById(customerID)
                .map(entity -> customerDto)
                .subscribe();
    }

    public Mono<Boolean> deleteCustomerById(Integer id){
        return this.customerRepository.deleteCustomerById(id);
    }

    /*
      1. THis is the default deleteById method provided by R2DBC repository.
      2 . Since this returns Mono<Void>, we cannot return any status to the caller whether
           delete was successful or not.
      3. Since this returns Mono<Void>, so in the controller .defaultIfEmpty(ResponseEntity.notFound().build())
           will  be executed.

     */
    public Mono<Void>deleteCustomerByIdDefaultR2DBCImpl(Integer id){
        return this.customerRepository.deleteById(id);
    }

}

