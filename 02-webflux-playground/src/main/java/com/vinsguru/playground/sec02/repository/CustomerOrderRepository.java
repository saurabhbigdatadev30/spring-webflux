package com.vinsguru.playground.sec02.repository;

import com.vinsguru.playground.sec02.dto.CustomerOrderRecord;
import com.vinsguru.playground.sec02.dto.OrderDetails;
import com.vinsguru.playground.sec02.entity.CustomerOrder;
import com.vinsguru.playground.sec02.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerOrderRepository extends ReactiveCrudRepository<CustomerOrder, UUID> {

    //

    /*

  TO DO -> Get the  Products details Ordered By a particular CustomerID
  We Have

     TABLE 1  =   CUSTOMER              =          CONTAINS                 [CUST_ID , name , email]
     TABLE 2  =   PRODUCT               =          CONTAINS                 [PRODUCT_ID , description , Price]
     TABLE 3  =   CUSTOMER_ORDER        =          CONTAINS                 [ORDER_ID, CUST_ID ,PRODUCT_ID,amount, order_date]

  (1) Join the tables CUSTOMER & CUSTOMER_ORDER on the field  CUST_ID (for the particular customer)
       so we get PRODUCT_ID

  (2) Join the result of (1) with the Products table on ProductID

     */
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
    Flux<Product> getProductsOrderedByCustomer(String name);



    // Record class field name= customerIdTest1Test2 mapped with DB column name = customer_id_Test1_Test2
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
