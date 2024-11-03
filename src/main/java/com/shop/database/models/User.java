package com.shop.database.models;

import java.sql.Timestamp;

public class User {
    private String username;
    private String new_username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    private Timestamp createdAt;

    public User(String username, String email, String firstName, String lastName, String password, String role) {
        // Конструктор без createdAt
        this.username = username;
        this.new_username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public User(String username, String email, String firstName, String lastName, String password, String role, Timestamp createdAt) {
        this.username = username;
        this.new_username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.new_username = username;
    }

    public String getNewUsername() {
        return new_username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

