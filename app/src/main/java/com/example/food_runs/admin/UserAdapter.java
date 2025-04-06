package com.example.food_runs.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_runs.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User_Model> userList;

    public UserAdapter(Context context, List<User_Model> userList) {
        this.context = context;
        this.userList = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserId, tvUserName, tvUserMobile, tvUserEmail;
        ImageButton btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserMobile = itemView.findViewById(R.id.tvUserMobile);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_model, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User_Model user = userList.get(position);

        holder.tvUserId.setText("ID: " + user.getId());
        holder.tvUserName.setText("Name: " + user.getName());
        holder.tvUserMobile.setText("Mobile: " + user.getMobile());
        holder.tvUserEmail.setText("Email: " + user.getEmail());

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(userList.get(currentPosition).getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        userList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, userList.size());
                                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
