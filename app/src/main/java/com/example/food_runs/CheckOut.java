package com.example.food_runs;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckOut extends AppCompatActivity implements PaymentResultListener {

    RecyclerView recyclerView;
    List<ItemCartModel> cartList;
    ItemCartAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userId;

    EditText editTextName, editTextPhone, editTextAddress, editTextEmail,
            editTextAltPhone, editTextLandmark, editTextPincode, editTextNote;

    Button btnPlaceOrder;
    RadioGroup radioGroupPayment;
    RadioButton radioCod, radioNetBanking;

    String selectedPaymentMethod = "";
    boolean isPaymentSuccessful = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        Checkout.preload(getApplicationContext());

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

        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioCod = findViewById(R.id.radioCod);
        radioNetBanking = findViewById(R.id.radioNetBanking);

        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCod) {
                selectedPaymentMethod = "COD";
                // Handle Cash on Delivery (COD) logic here
            } else if (checkedId == R.id.radioNetBanking) {
                selectedPaymentMethod = "Net Banking";
                // Trigger Razorpay payment immediately when 'Net Banking' is selected
                makePayment(); // Open Razorpay payment gateway immediately
            }
        });

        // Load cart items from Firebase if available
        loadCartItems();

        // Handle Place Order button click
        btnPlaceOrder.setOnClickListener(v -> {
            if (selectedPaymentMethod.equals("COD")) {
                isPaymentSuccessful = true;
                placeOrder();
            } else if (selectedPaymentMethod.equals("Net Banking")) {
                if (isPaymentSuccessful) {
                    placeOrder();
                } else {
                    Toast.makeText(this, "Please complete payment first", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void makePayment() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Step 1: Get User Email and Phone
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String contact = documentSnapshot.getString("phone");

                        // Step 2: Fetch MenuItems from Firestore and calculate total amount
                        db.collection("MenuItems") // Fetch from the "MenuItems" collection
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    int totalAmountInPaise = 0;

                                    for (DocumentSnapshot doc : querySnapshot) {
                                        Food_Model item = doc.toObject(Food_Model.class); // Assuming you are using Food_Model for menu items

                                        if (item != null) {
                                            // Assuming you have quantity in the UI or elsewhere to determine how many of each item the user wants
                                            int quantity = 1; // Here assuming quantity is 1 for each item in the menu, replace it with actual quantity

                                            // Parse the price and calculate the total amount
                                            String priceStr = item.getPrice().replaceAll("[^0-9.]", ""); // Remove any non-numeric characters
                                            double price = Double.parseDouble(priceStr);
                                            totalAmountInPaise += (int)(price * quantity * 100); // Convert price to paise (1 INR = 100 paise)
                                        }
                                    }

                                    // Step 3: Launch Razorpay for payment
                                    Checkout checkout = new Checkout();
                                    checkout.setKeyID("rzp_test_5fW6QYgf92sms3");

                                    try {
                                        JSONObject options = new JSONObject();
                                        options.put("name", "Food Runs");
                                        options.put("description", "Menu Item Payment");
                                        options.put("currency", "INR");
                                        options.put("amount", totalAmountInPaise); // in paise

                                        JSONObject prefill = new JSONObject();
                                        prefill.put("email", email);
                                        prefill.put("contact", contact);
                                        options.put("prefill", prefill);

                                        checkout.open(this, options);

                                    } catch (Exception e) {
                                        Log.e("Razorpay", "Checkout failed", e);
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to load menu items", Toast.LENGTH_SHORT).show();
                                    Log.e("Firestore", "MenuItems fetch error", e);
                                });

                    } else {
                        Toast.makeText(this, "User info not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "User fetch error", e);
                });
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
        if (!isPaymentSuccessful) {
            Toast.makeText(this, "Payment not completed", Toast.LENGTH_SHORT).show();
            return;
        }

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
        orderInfo.put("paymentMethod", selectedPaymentMethod);
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
        Intent intent = new Intent(CheckOut.this, OrderSuccess.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
        isPaymentSuccessful = true;
        placeOrder(); // Auto-place order after successful payment
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
        isPaymentSuccessful = false;
    }
}
