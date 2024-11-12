package com.shop.database.models;

public class ShoppingCartItem {
    private String owner;
    private Integer computerId;
    private Integer quantity;

    public ShoppingCartItem(String owner, Integer computerId, Integer quantity) {
        this.owner = owner;
        this.computerId = computerId;
        this.quantity = quantity;
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
