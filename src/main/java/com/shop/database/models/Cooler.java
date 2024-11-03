package com.shop.database.models;

import java.math.BigDecimal;

public class Cooler {
    private int id;
    private String name;
    private String brand;
    private String type;
    private BigDecimal coolingCapacity; // Using BigDecimal for precision
    private String link;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getCoolingCapacity() {
        return coolingCapacity;
    }

    public void setCoolingCapacity(BigDecimal coolingCapacity) {
        this.coolingCapacity = coolingCapacity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
