package com.example.food_runs.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_runs.LogIn_Page;
import com.example.food_runs.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UsersManageFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoData;
    private UserAdapter userAdapter;
    private List<User_Model> userList;
    private FirebaseFirestore db;

    public UsersManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_manage, container, false);

        // ✅ Toolbar setup
        Toolbar toolbar = view.findViewById(R.id.usertoolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            ImageView backArrow = view.findViewById(R.id.backArrow);
            if (backArrow != null) {
                backArrow.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Exit Admin Panel")
                                .setMessage("What would you like to do?")
                                .setPositiveButton("Go to Login", (dialog, which) -> {
                                    Intent intent = new Intent(getActivity(), LogIn_Page.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    getActivity().finish();
                                })
                                .setNegativeButton("Stay", (dialog, which) -> dialog.dismiss())
                                .setNeutralButton("Exit App", (dialog, which) -> getActivity().finishAffinity())
                                .show();
                    }
                });
            }
        }

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvNoData = view.findViewById(R.id.tvNoData);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), userList);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        fetchUsersFromFirestore();

        return view;
    }

    private void fetchUsersFromFirestore() {
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    Log.d("Firestore", "Total users: " + queryDocumentSnapshots.size());

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User_Model user = doc.toObject(User_Model.class);
                        if (user != null) {
                            user.setId(doc.getId());
                            userList.add(user);
                        }
                    }

                    userAdapter.notifyDataSetChanged();

                    if (userList.isEmpty()) {
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching users", e));
    }
}
