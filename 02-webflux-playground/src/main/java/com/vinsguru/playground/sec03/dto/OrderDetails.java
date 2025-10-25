package com.vinsguru.playground.sec03.dto;

import java.time.Instant;
import java.util.UUID;

/*
    This record represents the details of an order.
    joining the data from Order, Customer and Product tables.
 */
public record OrderDetails(UUID orderId,
                           Integer productId,
                           Integer customerId,
                           String customerNameTest1Test2,
                           String productNameTest1Test2,
                           Integer amount,
                           Instant orderDate) {
}
