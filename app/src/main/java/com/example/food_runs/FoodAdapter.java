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
                .placeholder(R.drawable.ic_exit) // Make sure you have placeholder.png in res/drawable
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, description, category, availability, price;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            title = itemView.findViewById(R.id.itemTitle);
            description = itemView.findViewById(R.id.itemDescription);
            category = itemView.findViewById(R.id.itemCategory);
            availability = itemView.findViewById(R.id.itemAvailability);
            price = itemView.findViewById(R.id.itemPrice);
        }
    }
}
