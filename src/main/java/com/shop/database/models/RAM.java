package com.shop.database.models;

public class RAM {
    private Integer id;
    private String name;
    private String brand;
    private Integer capacity; // in GB
    private Integer speed; // in MHz
    private String link;

    public RAM(Integer id, String name, String brand, Integer capacity, Integer speed, String link) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.capacity = capacity;
        this.speed = speed;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

