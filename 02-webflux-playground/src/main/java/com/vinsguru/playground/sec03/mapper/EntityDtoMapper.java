package com.vinsguru.playground.sec03.mapper;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import com.vinsguru.playground.sec03.entity.Customer;
import reactor.core.publisher.Mono;

public class EntityDtoMapper {

    // From Dto to Entity coming from the @RequestBody
    public static Customer toEntity(CustomerDto dto){
        var customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setId(dto.id());
        return customer;
    }

    // From Entity to Dto to be set in the @ResponseBody
    public static CustomerDto toDto(Customer customer){
         return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail()
        );
    }


}
