package com.example.food_runs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textview.MaterialTextView;

public class OrderSuccess extends AppCompatActivity {

    LottieAnimationView successLottie;
    TextView btnBack, btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_success);

        successLottie = findViewById(R.id.lottieAnimationView);
        btnBack = findViewById(R.id.btnBack);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        // Play Lottie for 4 seconds
        successLottie.setSpeed(1f); // Normal speed
        successLottie.playAnimation();

        new Handler().postDelayed(() -> {
            successLottie.pauseAnimation(); // Stops after 4 seconds
        }, 4000);

        // Go back to Home
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccess.this, MainActivity.class); // Change if your Home is another class
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Go to Item/Food Page
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccess.this, FoodStoreFragment.class); // Change this to your item page
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
