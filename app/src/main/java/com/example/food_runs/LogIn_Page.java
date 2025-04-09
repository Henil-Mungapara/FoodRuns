package com.example.food_runs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LogIn_Page extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    TextView SignUp, forgot;
    Button LogIn, LogInadmin;
    EditText etEmail, etPassword;

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);

        // UI Hooks
        SignUp = findViewById(R.id.tvLogin);
        LogIn = findViewById(R.id.btnLoginUser);
        LogInadmin = findViewById(R.id.btnLoginAdmin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        forgot = findViewById(R.id.tvForgotPassword);
        ImageView ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        // 🔄 Navigation
        SignUp.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUp_Page.class));
            finish();
        });

        forgot.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Forgot_Password.class));
            finish();
        });

        // 👁️ Toggle password
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

        // 👤 User login
        LogIn.setOnClickListener(v -> signInUser());

        // 🛠 Admin login with static credentials
        LogInadmin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.equals("henil@gmail.com") && password.equals("henil@23042005")) {
                Intent iNext = new Intent(getApplicationContext(), com.example.food_runs.admin.MainActivity.class); // Correct class
                startActivity(iNext);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LogIn_Page.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        LogIn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    LogIn.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn_Page.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogIn_Page.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LogIn_Page.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LogIn_Page.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_exit)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Stay", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Help", (dialog, which) ->
                        Toast.makeText(LogIn_Page.this, "Contact support@foodruns.com", Toast.LENGTH_LONG).show())
                .show();
    }
}
