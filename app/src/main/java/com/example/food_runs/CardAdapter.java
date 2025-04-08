package com.example.food_runs;

import android.content.Context;
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
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private List<CartItem> cartItemList;

    public CardAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.itemTitle.setText(cartItem.getTitle());
        holder.itemDescription.setText(cartItem.getDescription());
        holder.itemPrice.setText("₹" + cartItem.getPrice());
        holder.itemCategory.setText(cartItem.getCategory());

        Glide.with(context)
                .load(cartItem.getImageUrl())
                .placeholder(R.drawable.ic_buynow)
                .into(holder.itemImage);

        holder.deleteButton.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getUid();
            if (userId == null) return;

            String documentId = cartItem.getDocumentId();
            if (documentId == null || documentId.isEmpty()) {
                Toast.makeText(context, "Item ID missing", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // ✅ Ensure parent cart document is not deleted by setting dummy field
            db.collection("Cart")
                    .document(userId)
                    .set(Map.of("keep", true), SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        // 🔁 Now delete the actual item document
                        db.collection("Cart")
                                .document(userId)
                                .collection("items")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    cartItemList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, cartItemList.size());
                                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show();
                                });
                    });
        });


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage, deleteButton;
        TextView itemTitle, itemDescription, itemPrice, itemCategory;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
