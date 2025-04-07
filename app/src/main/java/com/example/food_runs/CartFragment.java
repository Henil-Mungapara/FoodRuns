package com.example.food_runs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItems = new ArrayList<>();
        cardAdapter = new CardAdapter(getContext(), cartItems);
        recyclerView.setAdapter(cardAdapter);

        loadCartItems();

        return view;
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("Cart")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    cartItems.clear();
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                        if (items != null) {
                            for (Map<String, Object> itemData : items) {
                                String title = (String) itemData.get("name");
                                String description = (String) itemData.get("description");
                                String price = (String) itemData.get("price");
                                String category = (String) itemData.get("category");
                                String imageUrl = (String) itemData.get("imageUrl");

                                CartItem cartItem = new CartItem(title, description, category, price, imageUrl);
                                cartItems.add(cartItem);
                            }
                            cardAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load cart.", Toast.LENGTH_SHORT).show());
    }
}
