package com.shop.database.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Order {
    private Integer id;
    private String customer;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItem> items;

    public Order(Integer id, String customer, Timestamp orderDate, BigDecimal totalAmount, String status, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setItems(List<OrderItem> itms) {
        this.items = itms;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
