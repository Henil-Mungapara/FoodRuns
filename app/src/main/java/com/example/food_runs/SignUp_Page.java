package com.example.food_runs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SignUp_Page extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    TextView login;
    Button SignUp;
    EditText etEmail, etPassword, etName, etMobile;

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        login = findViewById(R.id.tvLogin);
        SignUp = findViewById(R.id.btnSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);

        login.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogIn_Page.class));
            finish();
        });

        SignUp.setOnClickListener(v -> registerUser());

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

        // Name validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (name.length() < 2) {
            etName.setError("Name must be at least 2 characters");
            etName.requestFocus();
            return;
        }

        // Mobile validation
        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Mobile number is required");
            etMobile.requestFocus();
            return;
        }
        if (!mobile.matches("\\d{10}")) {
            etMobile.setError("Enter a valid 10-digit mobile number");
            etMobile.requestFocus();
            return;
        }

        // Email validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Internet check
        if (!isConnected()) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String uid = user.getUid();

                            // Format creation time
                            String creationTime;
                            if (user.getMetadata() != null) {
                                long timestamp = user.getMetadata().getCreationTimestamp();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                creationTime = sdf.format(new Date(timestamp));
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                creationTime = sdf.format(new Date());
                            }

                            // Store user details in Firestore
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("id", uid);
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("mobile", mobile);
                            userMap.put("password", password); // ⚠️ Consider hashing in production
                            userMap.put("creation_time", creationTime);

                            firestore.collection("Users").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUp_Page.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUp_Page.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUp_Page.this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }

                    } else {
                        progressDialog.dismiss();
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        Toast.makeText(SignUp_Page.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignUp_Page.this, MainActivity.class));
            finish();
        }
    }
}
