package com.vinsguru.playground.sec02.dto;

import java.time.Instant;
import java.util.UUID;

import java.time.Instant;
import java.util.UUID;

// This record class will store the result of the Join from customer & customer_order table
public record CustomerOrderRecord(
        Integer customerIdTest1Test2, // Record field name= customerIdTest1Test2 mapped with DB column name = customer_id_Test1_Test2
        String email,
        UUID orderId,
        Integer productID,
        String customerNameTest1Test2,
        Integer amount,
        Instant orderDate
) {

}
