package com.vinsguru.playground.sec02.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderDetails(UUID orderId,
                           String customerNameTest1Test2,
                           String productNameTest1Test2,
                           Integer amount,
                           Instant orderDate) {
}
