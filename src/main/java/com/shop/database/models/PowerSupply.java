package com.shop.database.models;


public class PowerSupply {
    private int id; 
    private String name; 
    private String brand;
    private int wattage; 
    private String efficiencyRating; 
    private String link;


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

    public int getWattage() { 
        return wattage; 
    } 
 
    public void setWattage(int wattage) { 
        this.wattage = wattage; 
    } 
 
    public String getEfficiencyRating() { 
        return efficiencyRating; 
    } 
 
    public void setEfficiencyRating(String efficiencyRating) { 
        this.efficiencyRating = efficiencyRating; 
    } 
 
    public String getLink() { 
        return link; 
    } 
 
    public void setLink(String link) { 
        this.link = link; 
    }
 }
 