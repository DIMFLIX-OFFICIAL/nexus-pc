package com.shop.database.models;

public class GraphicCard {
    private Integer id; 
    private String name; 
    private String brand; 
    private Integer memorySize; 
    private String memoryType; 
    private String link;

    public GraphicCard(Integer id, String name, String brand, Integer memorySize, String memoryType, String link) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.memorySize = memorySize;
        this.memoryType = memoryType;
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
 
    public Integer getMemorySize() { 
        return memorySize; 
    } 
 
    public void setMemorySize(Integer memorySize) { 
        this.memorySize = memorySize; 
    } 
 
    public String getMemoryType() { 
        return memoryType; 
    } 
 
    public void setMemoryType(String memoryType) { 
        this.memoryType = memoryType; 
    } 
 
    public String getLink() { 
        return link; 
    } 
 
    public void setLink(String link) { 
        this.link = link; 
    }
 }
 
