package com.example.food_runs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckOut extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ItemCartModel> cartList;
    ItemCartAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userId;

    EditText editTextName, editTextPhone, editTextAddress, editTextEmail,
            editTextAltPhone, editTextLandmark, editTextPincode, editTextNote;

    Button btnPlaceOrder;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerViewCartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        adapter = new ItemCartAdapter(this, cartList);
        recyclerView.setAdapter(adapter);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAltPhone = findViewById(R.id.editTextAltPhone);
        editTextLandmark = findViewById(R.id.editTextLandmark);
        editTextPincode = findViewById(R.id.editTextPincode);
        editTextNote = findViewById(R.id.editTextNote);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        Object itemData = getIntent().getSerializableExtra("itemData");
        if (itemData instanceof Food_Model) {
            Food_Model food = (Food_Model) itemData;

            ItemCartModel singleItem = new ItemCartModel();
            singleItem.setTitle(food.getTitle());
            singleItem.setPrice(food.getPrice());
            singleItem.setQuantity(1);
            singleItem.setImageUrl(food.getImageUrl());

            cartList.add(singleItem);
            adapter.notifyDataSetChanged();
        } else {
            loadCartItems();
        }

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartItems() {
        db.collection("Cart").document(userId).collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ItemCartModel item = doc.toObject(ItemCartModel.class);
                        cartList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void placeOrder() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String altPhone = editTextAltPhone.getText().toString().trim();
        String landmark = editTextLandmark.getText().toString().trim();
        String pincode = editTextPincode.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = db.collection("Orders").document(userId).collection("Orders").document().getId();

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("userId", userId);
        orderInfo.put("name", name);
        orderInfo.put("phone", phone);
        orderInfo.put("address", address);
        orderInfo.put("email", email);
        orderInfo.put("altPhone", altPhone);
        orderInfo.put("landmark", landmark);
        orderInfo.put("pincode", pincode);
        orderInfo.put("note", note);
        orderInfo.put("orderTime", FieldValue.serverTimestamp());

        db.collection("Orders").document(userId)
                .collection("Orders").document(orderId)
                .set(orderInfo)
                .addOnSuccessListener(aVoid -> {
                    List<Map<String, Object>> itemsList = new ArrayList<>();
                    int totalAmount = 0;

                    for (ItemCartModel item : cartList) {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("title", item.getTitle());
                        itemMap.put("price", item.getPrice());
                        itemMap.put("quantity", item.getQuantity());
                        itemMap.put("imageUrl", item.getImageUrl());

                        try {
                            totalAmount += Integer.parseInt(item.getPrice()) * item.getQuantity();
                        } catch (NumberFormatException ignored) {}

                        itemsList.add(itemMap);
                    }

                    Map<String, Object> singleItemWrap = new HashMap<>();
                    singleItemWrap.put("items", itemsList);
                    singleItemWrap.put("totalAmount", totalAmount);
                    singleItemWrap.put("totalItems", cartList.size());

                    db.collection("Orders").document(userId)
                            .collection("Orders").document(orderId)
                            .update(singleItemWrap)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(CheckOut.this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();
                                clearCart();
                                clearInputs();
                                redirectToStore();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CheckOut.this, "Failed to add items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CheckOut.this, "Order Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart() {
        CollectionReference cartRef = db.collection("Cart").document(userId).collection("items");
        cartRef.get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot) {
                doc.getReference().delete();
            }
            cartList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    private void clearInputs() {
        editTextName.setText("");
        editTextPhone.setText("");
        editTextAddress.setText("");
        editTextEmail.setText("");
        editTextAltPhone.setText("");
        editTextLandmark.setText("");
        editTextPincode.setText("");
        editTextNote.setText("");
    }

    private void redirectToStore() {
        Intent intent = new Intent(CheckOut.this, OrderSuccess.class); // or your shop screen
        startActivity(intent);
        finish();
    }
}
