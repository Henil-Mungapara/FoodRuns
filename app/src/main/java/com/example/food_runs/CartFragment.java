package com.example.food_runs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_runs.admin.AddItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CartItem> cartItems;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        setupToolbar(view);
        setupRecyclerView(view);
        loadCartItems();

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.carttoolbar);
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
                        bottomNav.setSelectedItemId(R.id.nav_item);
                    }
                });
            }
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItems = new ArrayList<>();
        cardAdapter = new CardAdapter(getContext(), cartItems);
        recyclerView.setAdapter(cardAdapter);
    }

    private void loadCartItems() {
        if (auth.getCurrentUser() == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("Cart")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    cartItems.clear();

                    if (querySnapshot.isEmpty() && getContext() != null) {
                        Toast.makeText(getContext(), "No items in your cart.", Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String title = document.getString("title");
                        String description = document.getString("description");
                        String category = document.getString("category");
                        String imageUrl = document.getString("imageUrl");

                        double price = 0.0;
                        try {
                            price = document.getDouble("price");
                        } catch (Exception e) {
                            try {
                                price = Double.parseDouble(document.getString("price"));
                            } catch (Exception ignored) {}
                        }

                        CartItem cartItem = new CartItem(title, description, category, price, imageUrl);
                        cartItem.setDocumentId(document.getId());
                        cartItems.add(cartItem);
                    }

                    cardAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
