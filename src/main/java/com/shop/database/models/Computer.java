package com.shop.database.models;
import java.math.BigDecimal;

public class Computer {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer processorId;
    private Integer graphicCardId; 
    private Integer motherboardId;
    private Integer ramId;
    private Integer ramsCount; 
    private Integer powerSupplyId;
    private Integer coolerId; 
    private Integer caseId;
    private String imageUrl;

    public Computer(Integer id, String name, String description, BigDecimal price,
                    Integer processorId, Integer graphicCardId, Integer motherboardId,
                    Integer ramId, Integer ramsCount, Integer powerSupplyId,
                    Integer coolerId, Integer caseId, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.processorId = processorId;
        this.graphicCardId = graphicCardId;
        this.motherboardId = motherboardId;
        this.ramId = ramId;
        this.ramsCount = ramsCount;
        this.powerSupplyId = powerSupplyId;
        this.coolerId = coolerId;
        this.caseId = caseId;
        this.imageUrl = imageUrl;
    }

    // Геттеры и сеттеры

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

    public Integer getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Integer processorId) {
        this.processorId = processorId;
    }

    public Integer getGraphicCardId() {
        return graphicCardId;
    }

    public void setGraphicCardId(Integer graphicCardId) {
        this.graphicCardId = graphicCardId;
    }

    public Integer getMotherboardId() {
        return motherboardId;
    }

    public void setMotherboardId(Integer motherboardId) {
        this.motherboardId = motherboardId;
    }

    public Integer getRamId() {
        return ramId;
    }

    public void setRamId(Integer ramId) {
        this.ramId = ramId;
    }

    public Integer getRamsCount() {
        return ramsCount;
    }

    public void setRamsCount(Integer ramsCount) {
        this.ramsCount = ramsCount;
    }

    public Integer getPowerSupplyId() {
        return powerSupplyId;
    }

    public void setPowerSupplyId(Integer powerSupplyId) {
        this.powerSupplyId = powerSupplyId;
    }

    public Integer getCoolerId() {
        return coolerId;
    }

    public void setCoolerId(Integer coolerId) {
        this.coolerId = coolerId;
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}
