package com.vinsguru.playground.tests.sec02;

import com.vinsguru.playground.sec02.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

public class Lec02ProductRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec02ProductRepositoryTest.class);

    @Autowired
    private ProductRepository repository;

    @Test
    public void findAllProducts() {
        this.repository.findAll()
                       .doOnNext(p -> log.info("{}", p))
                       .as(StepVerifier::create)
                       .expectNextCount(10)
                       .expectComplete()
                       .verify();
    }


    @Test
    public void findByPriceRange() {
        this.repository.findByPriceBetween(750, 1000)
                       .doOnNext(p -> log.info("{}", p))
                       .as(StepVerifier::create)
                       .expectNextCount(3)
                       .expectComplete()
                       .verify();
    }

    @Test
    public void findByPriceRangeThroughQuery() {
        this.repository.findByPriceBetweenQuery(750, 1000)
                .doOnNext(p -> log.info("{}", p))
                .as(StepVerifier::create)
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }



    @Test
    public void pageableTest() {
        this.repository.findBy(PageRequest.of(2, 4))
                .doOnNext(p -> log.info("{}", p))
                .subscribe();
    }

    /*
     a. When we set PageRequest.of(0,4)
        Page = 0
            Executing SQL statement [SELECT PRODUCT.ID, PRODUCT.DESCRIPTION, PRODUCT.PRICE FROM PRODUCT LIMIT 4]
             Product{id=1, description='iphone 20', price=750}
             Product{id=2, description='iphone 18', price=750}
             Product{id=3, description='ipad', price=800}
             Product{id=4, description='macbook pro', price=1200}

       b. When we set PageRequest.of(1,4) , we get  id=5 till id= 8
          Page = 1
             Executing SQL statement [SELECT PRODUCT.ID, PRODUCT.DESCRIPTION, PRODUCT.PRICE FROM PRODUCT OFFSET 4 ROWS FETCH FIRST 4 ROWS ONLY]
              Product{id=5, description='apple watch', price=400}
              Product{id=6, description='airpods', price=150}
              Product{id=7, description='airpods pro', price=250}
              Product{id=8, description='beats headphones', price=350}
     */




    @Test
    public void pageable() {
        this.repository.findBy(PageRequest.of(0, 3).withSort(Sort.by("price").ascending()))
                       .doOnNext(p -> log.info("{}", p))
                       .as(StepVerifier::create)
                       .assertNext(p -> Assertions.assertEquals(200, p.getPrice()))
                       .assertNext(p -> Assertions.assertEquals(250, p.getPrice()))
                       .assertNext(p -> Assertions.assertEquals(300, p.getPrice()))
                       .expectComplete()
                       .verify();
    }



    @Test
    public void pageableTest1() {
        this.repository.findBy(PageRequest.of(0, 3))
                .doOnNext(p -> log.info("{}", p))
                .subscribe();
    }

    @Test
    public void pageableTest2() {
        this.repository.findBy(PageRequest.of(1, 3))
                .doOnNext(p -> log.info("{}", p))
                .subscribe();
    }

    /*

    Page = 0
        Executing SQL statement [SELECT PRODUCT.ID, PRODUCT.DESCRIPTION, PRODUCT.PRICE FROM PRODUCT ORDER BY PRODUCT.PRICE ASC
          LIMIT 3]
           Product{id=9, description='apple tv', price=200}
           Product{id=7, description='airpods pro', price=250}
           Product{id=10, description='homepod', price=300}

      Page[1]
         main] o.s.r.c.DefaultDatabaseClient  : Executing SQL statement [SELECT PRODUCT.ID, PRODUCT.DESCRIPTION, PRODUCT.PRICE FROM PRODUCT ORDER BY PRODUCT.PRICE ASC OFFSET 3 ROWS FETCH FIRST 3 ROWS ONLY]
  main] t.s.Lec02ProductRepositoryTest : Product{id=5, description='apple watch', price=400}
  main] t.s.Lec02ProductRepositoryTest : Product{id=2, description='iphone 18', price=750}
  main] t.s.Lec02ProductRepositoryTest : Product{id=3, description='ipad', price=800}



     */

}
