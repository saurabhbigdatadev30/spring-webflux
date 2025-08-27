package com.vinsguru.playground.sec02.dto;

import java.time.Instant;
import java.util.UUID;

import java.time.Instant;
import java.util.UUID;

// This record class will store the result of the Join from customer & customer_order table

 /*
       Record class field name= customerIdTest1Test2 mapped with DB column name = customer_id_Test1_Test2
        customerIdTest1Test2    in the java class maps to    -> customer_id_Test1_Test2 in the DB
        customerNameTest1Test2  in the java class maps to     -> customer_name_Test1_Test2 in the DB
     */

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
