package com.vinsguru.playground.sec02.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Product {

    @Id
    private Integer id;
    @Column("description")
    private String description1;
    private Integer price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description1;
    }

    public void setDescription(String description) {
        this.description1 = description1;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description1 + '\'' +
                ", price=" + price +
                '}';
    }

}
