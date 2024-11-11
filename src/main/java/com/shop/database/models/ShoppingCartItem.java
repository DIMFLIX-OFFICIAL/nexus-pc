package com.shop.database.models;

public class ShoppingCartItem {
    private Integer id;
    private String owner;
    private Integer computerId;
    private Integer quantity;

    public ShoppingCartItem(Integer id, String owner, Integer computerId, Integer quantity) {
        this.id = id;
        this.owner = owner;
        this.computerId = computerId;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
