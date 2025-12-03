package com.vinsguru.playground.sec03.entity;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;
/*
	 Create an Entity class ....   In Spring Data (R2DBC / JDBC), the @Table annotation is optional
           a.Entity class name: CustomerOrder
           b.Default table name (by Spring Data Statergy is ): customer_order (CamelCase -> snake_case)
           Actual DB table name: customer_order .
           Conclusion: - Because the default mapping already matches the real table name, the @Table annotation is optional
                          and can be omitted

     Similarly ,  Using Spring Data JDBC/R2DBC with the default naming strategy.
       Field   [orderId]         -> column  [order_id]
       Field   [customerId]      -> column  [customer_id]
       Field   [productId]       -> column  [product_id]
       Field   [orderDate]       -> column  [order_date]

       If the actual column names follow this snake_case convention, we do not need @Column annotations.
                 */

public class CustomerOrder {

    @Id
    private UUID orderId;
    private Integer customerId;
    private Integer productId;
    private Integer amount;
    private Instant orderDate;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }
}
