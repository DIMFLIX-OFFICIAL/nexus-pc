package com.shop.database.models;

public class OrderResult {
    private boolean success;
    private String message;

    public OrderResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
