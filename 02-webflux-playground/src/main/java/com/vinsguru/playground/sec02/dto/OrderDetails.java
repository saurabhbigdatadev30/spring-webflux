package com.vinsguru.playground.sec02.dto;

import java.time.Instant;
import java.util.UUID;

/*
    This record represents the details of an order.
    joining the data from Order, Customer and Product tables.

    Field mapping with the database columns:
          - orderId                           ->   co.order_id
          - productId                         ->   co.product_id
          - customerId                        ->   c.id AS customer_id
          - customerNameTest1Test2            ->   c.name AS customer_name_test1_test2
          - productNameTest1Test2             ->   p.description AS product_name_test1_test2
          - amount                            ->   co.amount
          - orderDate                         ->   co.order_date

 */
public record OrderDetails(UUID orderId,
                           Integer productId,
                           Integer customerId,
                           String customerNameTest1Test2,
                           String productNameTest1Test2,
                           Integer amount,
                           Instant orderDate) {
}
