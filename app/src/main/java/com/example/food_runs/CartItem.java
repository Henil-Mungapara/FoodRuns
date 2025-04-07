package com.example.food_runs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CartItem implements Serializable {
    private String title;
    private String description;
    private String category;
    private String price;
    private String imageUrl;

    public CartItem() {}

    public CartItem(String title, String description, String category, String price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", title);
        map.put("description", description);
        map.put("category", category);
        map.put("price", price);
        map.put("imageUrl", imageUrl);
        return map;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
