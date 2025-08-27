package com.vinsguru.playground.sec02.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/*
    The @Entity annotation is required for JPA repositories, but for Spring Data R2DBC (reactive repositories),
    the entity class does not strictly require the @Entity annotation.
    R2DBC uses mapping based on the class structure and field names.
    So, your CustomerRepository will work with R2DBC even if Customer is not marked with @Entity
 */
@Table("customer")
public class Customer {

    @Id
    private Integer id;

    @Column("name")
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
