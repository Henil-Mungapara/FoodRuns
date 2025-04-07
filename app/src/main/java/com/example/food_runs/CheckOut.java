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

        // Bind all form inputs
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

        // Generate unique order ID under user
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
                    for (ItemCartModel item : cartList) {
                        db.collection("Orders").document(userId)
                                .collection("Orders").document(orderId)
                                .collection("Items")
                                .add(item);
                    }

                    Toast.makeText(CheckOut.this, "Order Placed Successfully!\nOrder ID: " + orderId, Toast.LENGTH_LONG).show();
                    clearCart();
                    clearInputs();
                    redirectToStore();
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
        Intent intent = new Intent(CheckOut.this, FoodStoreFragment.class); // replace with your main shop activity
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
