package com.example.food_runs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FoodStoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food_Model> foodList;

    private AutoCompleteTextView searchView;
    private ArrayAdapter<String> searchAdapter;
    private List<String> titleList;
    private List<Food_Model> allFoodItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_store, container, false);
        TextView noDataText = view.findViewById(R.id.tvNoData);

        // Toolbar setup
        Toolbar toolbar = view.findViewById(R.id.foodstoretoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView backArrow = view.findViewById(R.id.backArrow);
            backArrow.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new HomeFragment())
                        .commit();

                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                }
            });
        }

        recyclerView = view.findViewById(R.id.recyclerViewFood);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        foodList = new ArrayList<>();
        allFoodItems = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), foodList);
        recyclerView.setAdapter(adapter);

        // Setup Search
        searchView = view.findViewById(R.id.autoCompleteSearchfoodstorepage);
        titleList = new ArrayList<>();
        searchAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, titleList);
        searchView.setAdapter(searchAdapter);
        searchView.setThreshold(1); // show suggestions from 1st character

        searchView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String selectedTitle = adapterView.getItemAtPosition(position).toString();
            filterFoodList(selectedTitle);
        });

        fetchDataFromFirestore();

        return view;
    }

    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        db.collection("MenuItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    foodList.clear();
                    titleList.clear();
                    allFoodItems.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        if (getView() != null) {
                            TextView noDataText = getView().findViewById(R.id.tvNoData);
                            noDataText.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    if (getView() != null) {
                        TextView noDataText = getView().findViewById(R.id.tvNoData);
                        noDataText.setVisibility(View.GONE);
                    }

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Food_Model model = snapshot.toObject(Food_Model.class);
                        if (model == null) continue;

                        if (model.getTitle() != null) {
                            titleList.add(model.getTitle());
                        }

                        allFoodItems.add(model);

                        if (model.getImagePath() != null && !model.getImagePath().isEmpty()) {
                            storage.getReference().child(model.getImagePath())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        model.setImageUrl(uri.toString());
                                        foodList.add(model);
                                        adapter.notifyDataSetChanged();
                                        searchAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirebaseStorage", "Image load failed", e);
                                        Toast.makeText(getContext(), "Image failed to load", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            foodList.add(model);
                            adapter.notifyDataSetChanged();
                            searchAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading food items", Toast.LENGTH_SHORT).show();
                    if (getView() != null) {
                        TextView noDataText = getView().findViewById(R.id.tvNoData);
                        noDataText.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void filterFoodList(String title) {
        List<Food_Model> filteredList = new ArrayList<>();
        for (Food_Model item : allFoodItems) {
            if (item.getTitle() != null && item.getTitle().equalsIgnoreCase(title)) {
                filteredList.add(item);
            }
        }

        foodList.clear();
        foodList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}
