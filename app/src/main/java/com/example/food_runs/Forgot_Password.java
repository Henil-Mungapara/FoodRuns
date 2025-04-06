package com.example.food_runs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Forgot_Password extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnSubmitForgot;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSubmitForgot = findViewById(R.id.btnSubmitForgot);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmitForgot.setOnClickListener(v -> checkEmailAndSendReset());
    }

    private void checkEmailAndSendReset() {
        String email = etForgotEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking email...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    progressDialog.dismiss();
                    if (!querySnapshots.isEmpty()) {
                        // Email is found in Firestore
                        sendResetLink(email);
                    } else {
                        Toast.makeText(this, "Email is not registered", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendResetLink(String email) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending reset link...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_LONG).show();

                        // Redirect to login screen after short delay
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(Forgot_Password.this, LogIn_Page.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }, 1000);
                    } else {
                        Toast.makeText(this, "Failed to send reset link.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
