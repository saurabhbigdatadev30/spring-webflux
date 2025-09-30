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
    public void pageable() {
        /*
            Executing SQL statement with page 0 and size 3 and sort by price ascending is
            SELECT PRODUCT.ID, PRODUCT.DESCRIPTION, PRODUCT.PRICE FROM PRODUCT ORDER BY PRODUCT.PRICE ASC
              LIMIT 3
         */
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
    public void pageableTest() {
        this.repository.findBy(PageRequest.of(1, 3).withSort(Sort.by("price").descending()))
                .doOnNext(p -> log.info("{}", p))
                .subscribe()
                ;
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
