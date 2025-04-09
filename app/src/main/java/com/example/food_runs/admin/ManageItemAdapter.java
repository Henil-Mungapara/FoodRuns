package com.example.food_runs.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.food_runs.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ManageItemAdapter extends RecyclerView.Adapter<ManageItemAdapter.ManageViewHolder> {

    private Context context;
    private List<ManageItem> itemList;

    public ManageItemAdapter(Context context, List<ManageItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }




    @NonNull
    @Override
    public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.manage_item, parent, false);
        return new ManageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
        ManageItem item = itemList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvCategory.setText("Category: " + item.getCategory());
        holder.tvPrice.setText("₹" + item.getPrice());
        holder.tvAvailability.setText(item.getAvailability());

        // Change availability text color
        if ("Available".equalsIgnoreCase(item.getAvailability())) {
            holder.tvAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvAvailability.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Load image with Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.ivItemImage);

        // Handle Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Proceed with deletion
                        FirebaseFirestore.getInstance().collection("MenuItems")
                                .document(item.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                    itemList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, itemList.size()); // 🔁 important for RecyclerView consistency
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ManageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemImage;
        TextView tvTitle, tvCategory, tvPrice, tvAvailability;
        ImageButton btnDelete;

        public ManageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvAvailability = itemView.findViewById(R.id.tvItemAvailability);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
