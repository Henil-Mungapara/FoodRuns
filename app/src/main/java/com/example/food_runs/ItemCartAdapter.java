package com.example.food_runs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;

public class ItemCartAdapter extends RecyclerView.Adapter<ItemCartAdapter.CartViewHolder> {

    private Context context;
    private List<ItemCartModel> itemList;

    public ItemCartAdapter(Context context, List<ItemCartModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemcart_model, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ItemCartModel item = itemList.get(position);

        holder.textItemName.setText("🧆 Item: " + item.getTitle());
        holder.textItemQuantity.setText("🔢 Quantity: " + item.getQuantity());
        holder.textItemPrice.setText("💵 Price: ₹" + item.getPrice());

        // ✅ Null-safe Glide image loading
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_exit)
                    .error(R.drawable.ic_exit)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_exit);
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textItemName, textItemQuantity, textItemPrice;
        ImageView itemImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemName = itemView.findViewById(R.id.textItemName);
            textItemQuantity = itemView.findViewById(R.id.textItemQuantity);
            textItemPrice = itemView.findViewById(R.id.textItemPrice);
            itemImage = itemView.findViewById(R.id.itemImage); // ✅ FIXED to match XML
        }
    }
}
