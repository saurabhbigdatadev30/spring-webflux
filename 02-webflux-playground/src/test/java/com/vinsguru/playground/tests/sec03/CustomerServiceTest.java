package com.vinsguru.playground.tests.sec03;

import com.vinsguru.playground.sec03.dto.CustomerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AutoConfigureWebTestClient
@SpringBootTest(properties = "sec=sec03")
public class CustomerServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private WebTestClient client;

    @Test
    public void allCustomers() {
        this.client.get()
                   .uri("/customers/fetchAllCustomersX")
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectHeader().contentType(MediaType.APPLICATION_JSON)
                   .expectBodyList(CustomerDto.class)
                   .value(list -> log.info("Customer details == {}", list))
                   .hasSize(10);
    }

   @Test
   public void allCustomersRefactored() {
       this.client.get()
               .uri("/customers/fetchAllCustomersX")
               .exchange()
               .expectStatus().is2xxSuccessful()
               .expectHeader().contentType(MediaType.APPLICATION_JSON)
               .expectBodyList(CustomerDto.class)
               .value(list -> {
                   Assertions.assertNotNull(list);
                   Assertions.assertFalse(list.isEmpty(), "Expected at least 1 customer");
                   list.forEach(dto -> {
                       Assertions.assertNotNull(dto);
                       Assertions.assertNotNull(dto.id(), "id must not be null");
                       Assertions.assertNotNull(dto.name(), "name must not be null");
                       Assertions.assertNotNull(dto.email(), "email must not be null");
                   });
               });
   }

    @Test
    public void getCustomerById() {
        this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CustomerDto.class)
                .value(dto -> {
                      log.info("Customer details == {}", dto);
                      Assertions.assertNotNull(dto);
                      Assertions.assertEquals(1, dto.id());
                      Assertions.assertEquals("sam", dto.name());
                      Assertions.assertEquals("sam@gmail.com", dto.email());
                });
    }
/**
   1. Instead of mapping the response to CustomerDto.class i.e [expectBody(CustomerDto.class)]  we can use JSONPath to
       verify specific fields in JSON response.
   2.  This is useful when we want to verify only certain fields and not map the entire response to a POJO
   3.  JSONPath expressions allow us to navigate and extract specific parts of a JSON document easily.

 */

    @Test
    public void getCustomerByIdUsingJsonPath(){
        this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("sam")
                .jsonPath("$.email").isEqualTo("sam@gmail.com");
    }


    @Test
    public void fetchAllCustomers() {
        this.client.get()
                .uri("/customers/fetchAllCustomersX")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(r -> log.info("\n{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                .jsonPath("$.length()").isEqualTo(10)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("sam")
                .jsonPath("$[0].email").isEqualTo("sam@gmail.com")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("mike")
                .jsonPath("$[1].email").isEqualTo("mike@gmail.com")
               ;
    }


    @Test
    public void paginatedCustomerWithPojo(){
        this.client.get()
                .uri("/customers/paginated?page=2&size=3")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(CustomerDto.class)
                .value(customerLst -> {
                      customerLst.forEach(customerDto -> {
                          Assertions.assertNotNull(customerDto);
                          log.info("Customer details == {}", customerDto);
                      });
                });
    }


    @Test
    public void paginatedCustomersRefactoredWithJson() {
        this.client.get()
                   .uri("/customers/paginated?page=3&size=2")
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectBody()
                   .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                   .jsonPath("$.length()").isEqualTo(2)
                   .jsonPath("$[0].id").isEqualTo(5)
                   .jsonPath("$[1].id").isEqualTo(6)
                   .jsonPath("$[0].name").isEqualTo("sophia")
                   .jsonPath("$[1].name").isEqualTo("liam")
                   .jsonPath("$[0].email").isEqualTo("sophia@example.com")
                   .jsonPath("$[1].email").isEqualTo("liam@example.com")
                    ;
    }

    @Test
    public void createCustomer(){
        var customerDto = new CustomerDto(null, "oliver", "oliver@gmail.com");
      Mono<CustomerDto> mono = Mono.just(customerDto);
      this.client.post()
              .uri("/customers")
              // bodyValue takes raw data POJO, while body takes Publisher
              // .bodyValue(customerDto)
              .body(mono, CustomerDto.class)
              .exchange()
              .expectStatus().is2xxSuccessful()
              .expectBody()
              .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
              .jsonPath("$.id").isNotEmpty()
              .jsonPath("$.name").isEqualTo("oliver")
              .jsonPath("$.email").isEqualTo("oliver@gmail.com");
    }

    @Test
    public void createAndDeleteCustomer() {
        // create
         var dto = new CustomerDto(null, "marshal", "marshal@gmail.com");
         var monoCustomer = Mono.just(dto);

        this.client.post()
                   .uri("/customers")
                /*
                   understanding the difference between bodyValue vs body
                   bodyValue takes raw data POJO, while body takes Publisher
                 */
                   //.bodyValue(dto)
                   .body(monoCustomer, CustomerDto.class)
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectBody()
                   .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                   .jsonPath("$.id").isNotEmpty()
                   .jsonPath("$.id").isEqualTo(11)
                   .jsonPath("$.name").isEqualTo("marshal")
                   .jsonPath("$.email").isEqualTo("marshal@gmail.com");

        // delete
        this.client.delete()
                   .uri("/customers/11")
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectBody().isEmpty();
    }

    @Test
    public void updateCustomer() {
        var dto = new CustomerDto(null, "noel", "noel@gmail.com");
        this.client.put()
                   .uri("/customers/10")
                   .bodyValue(dto)
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectBody()
                   .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                   .jsonPath("$.id").isEqualTo(10)
                   .jsonPath("$.name").isEqualTo("noel")
                   .jsonPath("$.email").isEqualTo("noel@gmail.com");
    }

    @Test
    public void customerNotFound() {
        // get
        this.client.get()
                   .uri("/customers/11")
                   .exchange()
                   .expectStatus().is4xxClientError()
                   .expectBody().isEmpty();

        // delete
        this.client.delete()
                   .uri("/customers/11")
                   .exchange()
                   .expectStatus().is4xxClientError()
                   .expectBody().isEmpty();

        // put
        var dto = new CustomerDto(null, "noel", "noel@gmail.com");
        this.client.put()
                   .uri("/customers/11")
                   .bodyValue(dto)
                   .exchange()
                   .expectStatus().is4xxClientError()
                   .expectBody().isEmpty();
    }

}
