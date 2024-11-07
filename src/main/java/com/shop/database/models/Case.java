package com.shop.database.models;

public class Case implements Identifiable {
    private Integer id; 
    private String name; 
    private String brand; 
    private String formFactor; 
    private String color; 
    private String link;

    public Case(Integer id, String name, String brand, String formFactor, String color, String link) {
        this.id = id; 
        this.name = name; 
        this.brand = brand; 
        this.formFactor = formFactor; 
        this.color = color; 
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
 
    public String getFormFactor() { 
        return formFactor; 
    } 
 
    public void setFormFactor(String formFactor) { 
        this.formFactor = formFactor; 
    } 
 
    public String getColor() { 
        return color; 
    } 
 
    public void setColor(String color) { 
        this.color = color; 
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
 
