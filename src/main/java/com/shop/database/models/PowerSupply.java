package com.shop.database.models;


public class PowerSupply implements Identifiable {
    private Integer id; 
    private String name; 
    private String brand;
    private Integer wattage; 
    private String efficiencyRating; 
    private String link;

    public PowerSupply(Integer id, String name, String brand, Integer wattage, String efficiencyRating, String link) {
        this.id = id; 
        this.name = name; 
        this.brand = brand; 
        this.wattage = wattage; 
        this.efficiencyRating = efficiencyRating; 
        this.link = link;
    }


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

    public Integer getWattage() { 
        return wattage; 
    } 
 
    public void setWattage(Integer wattage) { 
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

    @Override
    public String toString() {
        return name;
    }
 }
 