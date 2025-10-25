package com.vinsguru.playground.sec03.repository;


import com.vinsguru.playground.sec03.dto.CustomerOrderRecord;
import com.vinsguru.playground.sec03.dto.OrderDetails;
import com.vinsguru.playground.sec03.entity.CustomerOrder;
import com.vinsguru.playground.sec03.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerOrderNativeRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {


    @Query("""
       SELECT
            p.*
        FROM
            customer c
        INNER JOIN customer_order co ON c.id = co.customer_id 
        INNER JOIN product p ON co.product_id = p.id
        WHERE
            c.name = :name
       """)
    Flux<Product> getProductsDetailsOrderedByCustomer(String name);


    @Query("""
           SELECT
            c.id as customer_id_Test1_Test2,
            c.email,
            co.order_id,
            co.product_id,
            c.name AS customer_name_Test1_Test2,
            co.amount,
            co.order_date
                FROM
                    customer c
                INNER JOIN customer_order co ON c.id = co.customer_id
                 WHERE
                 c.name = :name
            """)
    Flux<CustomerOrderRecord> getOrderDetailsJoin(String name);



    @Query("""
        SELECT
            co.order_id,
            co.product_id,
            c.id as customer_id,
            c.name AS customer_name_test1_test2,
            p.description AS product_name_test1_test2,
            co.amount,
            co.order_date
        FROM
            customer c
        INNER JOIN customer_order co ON c.id = co.customer_id
        INNER JOIN product p ON co.product_id = p.id
        WHERE
            p.description = :description
        ORDER BY co.amount DESC
        """)
    Flux<OrderDetails> getOrderDetailsByProduct(String description);




}
