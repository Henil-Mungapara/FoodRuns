package com.example.food_runs;

import java.io.Serializable;

public class Food_Model implements Serializable {
    private String title;
    private String description;
    private String price;
    private String category;
    private String availability;
    private String imagePath;
    private String imageUrl;

    public Food_Model() {
        // Required for Firebase
    }

    public Food_Model(String title, String description, String price, String category, String availability, String imagePath, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.availability = availability;
        this.imagePath = imagePath;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getAvailability() { return availability; }
    public String getImagePath() { return imagePath; }
    public String getImageUrl() { return imageUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
