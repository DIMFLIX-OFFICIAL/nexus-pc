package com.shop.database.models;

import java.math.BigDecimal;

public class Computer {
    private int id;
    private String name;
    private String description;
    private BigDecimal price; // Using BigDecimal for currency precision
    private int processorId; // Foreign key reference to Processor
    private int graphicsCardId; // Foreign key reference to Graphics Card
    private int powerSupplyId; // Foreign key reference to Power Supply
    private int stockQuantity;
    private String imagePath;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getProcessorId() {
        return processorId;
    }

    public void setProcessorId(int processorId) {
        this.processorId = processorId;
    }

    public int getGraphicsCardId() {
        return graphicsCardId;
    }

    public void setGraphicsCardId(int graphicsCardId) {
        this.graphicsCardId = graphicsCardId;
    }

   public int getPowerSupplyId() { 
       return powerSupplyId; 
   } 

   public void setPowerSupplyId(int powerSupplyId) { 
       this.powerSupplyId = powerSupplyId; 
   } 

   public int getStockQuantity() { 
       return stockQuantity; 
   } 

   public void setStockQuantity(int stockQuantity) { 
       this.stockQuantity = stockQuantity; 
   } 

   public String getImagePath() { 
       return imagePath; 
   } 

   public void setImagePath(String imagePath) { 
       this.imagePath = imagePath; 
   }
}
