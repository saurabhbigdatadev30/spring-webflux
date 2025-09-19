package com.vinsguru.playground.sec03.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("traditional")
@Slf4j
public class TraditionalWebController {


    // We no longer use RestTemplate since it is deprecated
    // private final RestTemplate restTemplate = new RestTemplate();
    private final RestClient restClient = RestClient.builder()
                                                    .requestFactory(new JdkClientHttpRequestFactory())
                                                    .baseUrl("http://localhost:7070")
                                                    .build();

    @GetMapping("products")
    public List<Product> getProducts() {
        var list = this.restClient.get()
                                  .uri("/demo01/products")
                                  .retrieve()
                                  .body(new ParameterizedTypeReference<List<Product>>() {
                                  });
        log.info("received response: {}", list);
        return list;
    }

    /*
        This method returns a Flux<Product> instead of List<Product>.
        We convert the List<Product> to Flux<Product> using Flux.fromIterable().
        This is blocking code since we are using RestClient which is a blocking client.
        In a real-world scenario, we would use a non-blocking client like WebClient to
        make non-blocking calls to the external service.
     */
    @GetMapping("products2")
    public Flux<Product> getProducts2() {
        var listProducts = this.restClient.get()
                                  .uri("/demo01/products")
                                  .retrieve()
                                  .body(new ParameterizedTypeReference<List<Product>>() {
                                  });
        log.info("received response: {}", listProducts);
        return Flux.fromIterable(listProducts);
    }

}
