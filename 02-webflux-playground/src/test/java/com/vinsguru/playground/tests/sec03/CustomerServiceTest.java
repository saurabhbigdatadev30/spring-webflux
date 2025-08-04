package com.vinsguru.playground.tests.sec03;

import com.vinsguru.playground.sec03.dto.CustomerDto;
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
                   .uri("/customers")
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectHeader().contentType(MediaType.APPLICATION_JSON)
                   .expectBodyList(CustomerDto.class)
                   .value(list -> log.info("{}", list))
                   .hasSize(10);
    }



    @Test
    public void paginatedCustomers() {
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

    /*
                      page = 3  &  size = 2  returns 2 JSON elements JSON []
                           [
                              {"id":5,"name":"sophia","email":"sophia@example.com"},
                              {"id":6,"name":"liam","email":"liam@example.com"}
                            ]
     */



    @Test
    public void customerById() {
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
              /*
                customerById returns 1 JSON , not array ,
                 {"id":1,"name":"sam","email":"sam@gmail.com"}


                  so we don't need

                 .jsonPath("$[0].name").isEqualTo("sam")


               */

    @Test
    public void createAndDeleteCustomer() {
        // create
         var dto = new CustomerDto(null, "marshal", "marshal@gmail.com");
         var monoCustomer = Mono.just(dto);

        this.client.post()
                   .uri("/customers")
                /*
                   understanding the difference between bodyValue vs body
                   bodyValue takes raw data , while body takes Publisher
                 */
                   //.bodyValue(dto)
                   .body(monoCustomer, CustomerDto.class)
                   .exchange()
                   .expectStatus().is2xxSuccessful()
                   .expectBody()
                   .consumeWith(r -> log.info("{}", new String(Objects.requireNonNull(r.getResponseBody()))))
                   .jsonPath("$.id").isNotEmpty();
                //  .jsonPath("$.id").isEqualTo(11)
                 //  .jsonPath("$.name").isEqualTo("marshal")
                //   .jsonPath("$.email").isEqualTo("marshal@gmail.com");

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
