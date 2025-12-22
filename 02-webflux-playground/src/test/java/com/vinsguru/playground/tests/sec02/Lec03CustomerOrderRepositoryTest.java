package com.vinsguru.playground.tests.sec02;

import com.vinsguru.playground.sec02.repository.CustomerOrderRepository;
import com.vinsguru.playground.sec03.repository.CustomerOrderNativeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

public class Lec03CustomerOrderRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec03CustomerOrderRepositoryTest.class);

    @Autowired
    private CustomerOrderRepository repository;



    @Test
    public void productsOrderedByCustomer() {
       this.repository.getProductsOrderedByCustomer("mike")
                      .doOnNext(p -> log.info("{}", p))
                      .as(StepVerifier::create)
                      .expectNextCount(2)
                      .expectComplete()
                      .verify();
    }

    @Test
    public void getProductDetailsByCustomerName(){
        this.repository.getProductsOrderedByCustomer("mike")
                .doOnNext(c -> log.info("Product details : - {} ", c))
                .as(StepVerifier::create)
                .assertNext(p -> Assertions.assertEquals("iphone 20" , p.getDescription()))
                .assertNext(p -> Assertions.assertEquals("mac pro", p.getDescription()))
                .expectComplete()
                .verify();

    }



    // This test is to check the join between Customer and CustomerOrder to fetch Flux of OrderDetails for a given customer
    @Test
    public void getCustomerOrderDetails() {
          this.repository.getOrderDetailsByCustomerName("sam")
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }


    // To DO -> Test the order details and customer details for a given product
    @Test
    public void orderDetailsByProduct() {
        this.repository.getOrderDetailsByProductDescription("iphone 20")
                       .doOnNext(dto -> log.info("Get{}", dto))
                       .as(StepVerifier::create)
                       .assertNext(dto -> Assertions.assertEquals(975, dto.amount()))
                       .assertNext(dto -> Assertions.assertEquals(950, dto.amount()))
                       .expectComplete()
                       .verify();
    }

}
