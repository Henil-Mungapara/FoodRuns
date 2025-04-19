package com.example.food_runs;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

public class Edit_Profile extends AppCompatActivity {

    private EditText etName, etEmail, etMobile;
    private Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        btnSaveChanges = findViewById(R.id.btnSave);

        // Load user data into EditText fields
        loadUserProfile();

        // Save Changes Button click listener
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        firestore.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String mobile = documentSnapshot.getString("mobile");

                        // Set data to EditText fields
                        etName.setText(name);
                        etEmail.setText(email);
                        etMobile.setText(mobile);
                    } else {
                        Toast.makeText(Edit_Profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Edit_Profile.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveChanges() {
        String updatedName = etName.getText().toString().trim();
        String updatedEmail = etEmail.getText().toString().trim();
        String updatedMobile = etMobile.getText().toString().trim();

        if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedMobile.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            User updatedUser = new User(updatedName, updatedEmail, updatedMobile, uid);

            // Update user data in Firestore
            firestore.collection("Users").document(uid)
                    .set(updatedUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Edit_Profile.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();  // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Edit_Profile.this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
