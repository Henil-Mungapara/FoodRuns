package com.example.food_runs;

public class Food_Model {
    private String title;
    private String description;
    private String price;
    private String category;
    private String availability;
    private String imagePath;  // <-- important!
    private String imageUrl;

    public Food_Model() {
        // Empty constructor needed for Firestore
    }

    public Food_Model(String title, String description, String price, String category, String availability, String imagePath) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.availability = availability;
        this.imagePath = imagePath;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getCategory() { return category; }
    public String getAvailability() { return availability; }
    public String getImagePath() { return imagePath; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
