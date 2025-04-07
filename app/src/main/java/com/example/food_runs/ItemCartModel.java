package com.example.food_runs;

import java.io.Serializable;

public class ItemCartModel implements Serializable {
    private String title;
    private int quantity;
    private String price;
    private String imageUrl;

    public ItemCartModel() {
        // Required for Firebase deserialization
    }

    public ItemCartModel(String title, int quantity, String price, String imageUrl) {
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
