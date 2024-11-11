package com.shop.database.models;

public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer computerId;
    private Integer quantity;

    public OrderItem(Integer id, Integer orderId, Integer computerId, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.computerId = computerId;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getComputerId() {
        return computerId;
    }

    public void setComputerId(Integer computerId) {
        this.computerId = computerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

