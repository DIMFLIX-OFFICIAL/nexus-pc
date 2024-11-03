package com.shop.database.models;

public class GraphicsCard {
    private int id; 
    private String name; 
    private String brand; 
    private int memorySize; 
    private String memoryType; 
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
 
    public int getMemorySize() { 
        return memorySize; 
    } 
 
    public void setMemorySize(int memorySize) { 
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
 
