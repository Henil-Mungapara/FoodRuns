package com.example.food_runs.admin;

public class User_Model {
    private String id;
    private String name;
    private String mobile;
    private String email;

    // Empty constructor (required for Firestore)
    public User_Model() {
    }

    // Full constructor
    public User_Model(String id, String name, String mobile, String email) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
