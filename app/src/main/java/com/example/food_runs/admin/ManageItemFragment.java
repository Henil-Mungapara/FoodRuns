package com.example.food_runs.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_runs.CartFragment;
import com.example.food_runs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ManageItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvNoData;
    private ManageItemAdapter adapter;
    private List<ManageItem> itemList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_item, container, false);

        // ======== Toolbar setup ========
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = view.findViewById(R.id.manageitemtoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView backArrow = view.findViewById(R.id.backArrow);
            if (backArrow != null) {
                backArrow.setOnClickListener(v -> {
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new AddItemFragment())
                            .commit();

                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_add);
                    }
                });
            }
        }

        // ======== RecyclerView setup ========
        recyclerView = view.findViewById(R.id.recyclerViewFood);
        tvNoData = view.findViewById(R.id.tvNoData);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemList = new ArrayList<>();
        adapter = new ManageItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchItems();

        return view;
    }

    // ======== Firestore fetch logic ========
    private void fetchItems() {
        db.collection("MenuItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ManageItem item = doc.toObject(ManageItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            itemList.add(item);
                        }
                    }

                    if (itemList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
