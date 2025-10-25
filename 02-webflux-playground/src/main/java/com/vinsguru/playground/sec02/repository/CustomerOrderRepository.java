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

    /*
       TO DO -> Get the Products details Ordered By a particular CustomerID (Will be using the Join)
                We Have 3 Tables
                   TABLE 1  =>   CUSTOMER        =  CONTAINS    [CUST_ID , name , email]
                   TABLE 2  =>   PRODUCT         =  CONTAINS    [PRODUCT_ID , description , Price]
                   TABLE 3  =>   CUSTOMER_ORDER  =  CONTAINS    [ORDER_ID, CUST_ID ,PRODUCT_ID,amount, order_date]


     1. [CUSTOMER]  & [CUSTOMER_ORDER]  are joined on CUST_ID
        So we can get the customer order details & customer details for a particular CUSTOMER_ID which also contains the PRODUCT_ID.
             [ORDER_ID, CUST_ID , PRODUCT_ID , amount, order_date , name , email]


     2. [CUSTOMER_ORDER] & [PRODUCT] are joined on PRODUCT_ID
           So we can get the PRODUCT details for a particular PRODUCT_ID.


     3. We need to get the PRODUCT details for a particular CUSTOMER_ID


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



    /*
      The query parameters will be automatically mapped to [CustomerOrderRecord] class fields

       Record class [CustomerOrderRecord] is used to hold the result of the query , having the following fields
             orderID                ->    co.order_id,
             productID              ->    co.product_id,
             customerIdTest1Test2    ->   customer_id_Test1_Test2
             customerNameTest1Test2  ->   customer_name_Test1_Test2
     */

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

    /*
      OrderDetails.java class              Holds the result of the query , having the following fields
             orderID                    <==         co.order_id,
             productID                  <==         co.product_id,
             customerIdTest1Test2       <==         customer_id_Test1_Test2
             customerNameTest1Test2     <==         customer_name_Test1_Test2
             productNameTest1Test2      <==         product_name_Test1_Test2
             amount                     <==         co.amount
             orderDate                  <==         co.order_date
     */

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
