package com.shop.database.models;

public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Computer computer;
    private Integer quantity;

    public OrderItem(Integer id, Integer orderId, Computer pc, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.computer = pc;
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

    public Computer getComputer() {
        return computer;
    }

    public void setComputer(Computer pc) {
        this.computer = pc;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

