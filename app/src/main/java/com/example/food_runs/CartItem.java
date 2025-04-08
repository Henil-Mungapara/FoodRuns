package com.example.food_runs;

import java.util.HashMap;
import java.util.Map;

public class CartItem {
    private String title;
    private String description;
    private String category;
    private double price;
    private String imageUrl;

    // ✅ Firestore document ID (needed to delete the item)
    private String documentId;

    public CartItem() {
        // Required for Firestore
    }

    public CartItem(String title, String description, String category, double price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    // For Firestore arrayRemove fallback (not used now, but good to keep)
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("category", category);
        map.put("price", price);
        map.put("imageUrl", imageUrl);
        return map;
    }
}
