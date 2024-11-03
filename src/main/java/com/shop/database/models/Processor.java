package com.shop.database.models;

import java.math.BigDecimal;

public class Processor {
    private int id;
    private String name;
    private String brand;
    private int cores;
    private int threads;
    private BigDecimal baseClock; // Using BigDecimal for decimal values
    private BigDecimal boostClock; // Using BigDecimal for decimal values
    private String link;

    public Processor(int id, String name, String brand, int cores, int threads, BigDecimal baseClock, BigDecimal boostClock, String link) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.cores = cores;
        this.threads = threads;
        this.baseClock = baseClock;
        this.boostClock = boostClock;
        this.link = link;
    }

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

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public BigDecimal getBaseClock() {
        return baseClock;
    }

    public void setBaseClock(BigDecimal baseClock) {
        this.baseClock = baseClock;
    }

    public BigDecimal getBoostClock() {
        return boostClock;
    }

    public void setBoostClock(BigDecimal boostClock) {
        this.boostClock = boostClock;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

