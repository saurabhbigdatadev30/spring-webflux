package com.vinsguru.playground.sec03.entity;

import org.springframework.data.annotation.Id;
/*
    This is a simple Customer entity class with fields for id, name, and email.
    @Entity annotation is not used here, but typically it would be used to mark this class as a JPA entity.
     @Entity is not mandatory for Spring R2 Data repositories to work, but it's a common practice.

 */

public class Customer {

    @Id
    private Integer id;
    private String name;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
