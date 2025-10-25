package com.vinsguru.playground.sec03.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Flux<CustomerDto> allCustomers() {
        return this.customerService.getAllCustomers();
    }

    @GetMapping("/fetchAllCustomersX")
    public Flux<CustomerDto> fetchAllCustomers() {
        return this.customerService.fetchAllCustomers();
    }

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


       /*

               (a)  If the customer is found , the this.customerService.deleteCustomerById(id) deletes &   returns empty

               (b)  If the customer is not found, then also the this.customerService.deleteCustomerById(id) returns empty



                To address this issue we use @Query = Modifying
                        Mono<Integer> deleteByLastName  - Number of  rows were affected
                        Mono<Void> deleteByLastName -
                        Mono<Boolean> deleteByLastName  - If the rows were affected


                         @Modifying // for demo
                         @Query("delete from customer where id=:id")
                         Mono<Boolean> deleteCustomerById(Integer id);

                So now in the @Controller

                   public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {

                         return this.customerService.deleteCustomerById(id) returns boolean

                         Using filter(b ->b), this checks that this.customerService.deleteCustomerById(id) returns true or false
                                   (a) If it returns true , then   .map(b -> ResponseEntity.ok().<Void>build())
                                   (b) If it returns false , then  .defaultIfEmpty(ResponseEntity.notFound().build());


         */
    }

}
