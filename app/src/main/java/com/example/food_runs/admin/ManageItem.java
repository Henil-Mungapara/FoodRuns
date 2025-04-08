package com.example.food_runs.admin;

public class ManageItem {
    private String id;
    private String title;
    private String description;
    private String price;
    private String category;
    private String imageUrl;
    private String availability;

    // Required empty constructor for Firebase
    public ManageItem() {}

    public ManageItem(String id, String title, String description, String price, String category, String imageUrl, String availability) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.availability = availability;
    }

    // 🔹 Add Getter & Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Other getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAvailability() {
        return availability;
    }
}
