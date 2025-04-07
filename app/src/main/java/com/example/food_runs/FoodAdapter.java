package com.example.food_runs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<Food_Model> foodList;

    public FoodAdapter(Context context, List<Food_Model> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_model, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food_Model model = foodList.get(position);

        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.category.setText(model.getCategory());
        holder.price.setText("₹" + model.getPrice());

        if (model.getAvailability() != null && model.getAvailability().equalsIgnoreCase("Available")) {
            holder.availability.setText("Available");
            holder.availability.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.availability.setText("Not Available");
            holder.availability.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        Glide.with(context)
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_exit)
                .into(holder.imageView);

        // 🔘 Buy Now button functionality
        holder.buyNowIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, CheckOut.class);
            intent.putExtra("itemData", model);  // Pass food item data to CheckOut Activity
            context.startActivity(intent);
        });

        // 🔘 Add to Cart button functionality
        holder.addToCartIcon.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (userId == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique ID for the cart item
            String itemId = UUID.randomUUID().toString();

            Map<String, Object> item = new HashMap<>();
            item.put("itemId", itemId);
            item.put("title", model.getTitle());
            item.put("description", model.getDescription());
            item.put("category", model.getCategory());
            item.put("price", model.getPrice());
            item.put("imageUrl", model.getImageUrl());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Cart").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            db.collection("Cart").document(userId)
                                    .update("items", FieldValue.arrayUnion(item))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                        Log.e("FirestoreError", "Error adding item to cart: ", e);
                                    });
                        } else {
                            Map<String, Object> cartData = new HashMap<>();
                            cartData.put("items", FieldValue.arrayUnion(item));
                            db.collection("Cart").document(userId)
                                    .set(cartData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to create cart", Toast.LENGTH_SHORT).show();
                                        Log.e("FirestoreError", "Error creating cart document: ", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to check cart document", Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreError", "Error checking cart document: ", e);
                    });
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, buyNowIcon, addToCartIcon;
        TextView title, description, category, availability, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            title = itemView.findViewById(R.id.itemTitle);
            description = itemView.findViewById(R.id.itemDescription);
            category = itemView.findViewById(R.id.itemCategory);
            availability = itemView.findViewById(R.id.itemAvailability);
            price = itemView.findViewById(R.id.itemPrice);
            buyNowIcon = itemView.findViewById(R.id.btnBuyNowIcon);
            addToCartIcon = itemView.findViewById(R.id.btnAddToCartIcon);
        }
    }
}
