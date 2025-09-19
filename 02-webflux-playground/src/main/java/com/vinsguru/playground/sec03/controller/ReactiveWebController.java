package com.vinsguru.playground.sec03.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("reactive")
public class ReactiveWebController {

    private static final Logger log = LoggerFactory.getLogger(ReactiveWebController.class);
    private final WebClient webClient = WebClient.builder()
                                                 .baseUrl("http://localhost:7070")
                                                 .build();

    /*
        (1) We have a client service that will invoke the Remote Service i.e the Customer Service invokes the Products Service

               (a) From the Customer Service we invoke Products MS to get all the Products() , using RestClient or WebClient
               (b) We use Reactive Pipeline using WebClient , to invoke the Products MS.

               (c) The Products MS takes 1 sec to return a product. So total time taken to return all the products
                   will be ->
                         No of Products * 1 Sec = <> , in case of traditional approach .
                         So if we have n =10 products, we get the response only after 10 sec.

            In case of Flux , we will get a Stream of response i.e every sec we get the product until the last product

                       In sec 1 = Product[1]  -> is returned
                       In sec 2 = Product[2]  -> is returned
                       In sec 3 = Product[3]  -> is returned
                       In sec 4 = Product[4]  -> is returned
                       ......................
                       ........................
                       .....................

          In case of Traditional the thread is blocked until  all the products are returned  , so we get output after 10 sec

        (2) The Flux is sort of pipeline, one end connects with the remote service while the other end is the receiver

        (3) The data is transferred through the pipeline in streaming , as and when the data is produced by service B is
            received by the Service 1

            Subscriber -> Publisher (Reactive Stream)
                (a) WebClient to invoke a remote service
                (b) R2DBC to invoke DB Repository


                getProducts()
                .subscribe()

    Summary ----------------------------------------------------------------------

    (1)  When the client asks for the product, the WebClient immediately call the product service, and whatever it sends,
         we are able to get it quickly and share it with our caller via flux.

    (2)  The caller does not have to wait for 10s for the execution to complete .
         Instead, we can stream the response as and when we get it, we expose it, or we emit via the flux to the caller

    (3)  When the caller cancels the request, we can detect and react quickly. So the reactive microservices is all about
         being responsive. Entire Stack should be reactive i.e R2DBC , Reactive Kafka , WebClient

      */

    @GetMapping("products")
    public Flux<Product> getProducts() {
        return this.webClient.get()
                             .uri("/demo01/products")
                             .retrieve()
                             .bodyToFlux(Product.class)
                             .doOnNext(p -> log.info("received: {}", p));
    }

    @GetMapping(value = "products/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getProductsStream() {
        /*
           As and when we get an item from the remote service, it will return to doOnNext()
           When a client closes browser it internally invokes the subscription.cancel() method , which we can
           capture using doOnCancel()

           When we subscribe , the Subscriber gets the subscription object.
           Using this subscription object the Subscriber can request request(n) items from  the producer.
         */
        return this.webClient.get()
                             .uri("/demo01/products")
                             .retrieve()
                             .bodyToFlux(Product.class)
                             .doOnCancel(() -> log.info("Request Cancelled by the Client"))
                             .doOnNext(p -> log.info("received: {}", p))
                            /*
                              After processing 4 records the Publisher crashed .  However,  it was able to give
                              the partial response & throws some error i.e  exited with the error code.
                              Using onErrorComplete , completes the Flux stream when an error occurs, instead of propagating the
                              error downstream.
                              This means subscribers will not receive an error signal; the stream will just end normally
                             if any error happens.
                             */
                             .onErrorComplete();
    }

}
