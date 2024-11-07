package com.shop.database.models;

public class Motherboard implements Identifiable {
    private Integer id;
    private String name;
    private String brand;
    private String socketType;
    private String formFactor;
    private Integer maxMemory;
    private String link;

    public Motherboard(Integer id, String name, String brand, String socketType, String formFactor, Integer maxMemory, String link) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.socketType = socketType;
        this.formFactor = formFactor;
        this.maxMemory = maxMemory;
        this.link = link;
    }

    // Getters and Setters
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSocketType() {
        return socketType;
    }

    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public Integer getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Integer maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return name;
    }
}

