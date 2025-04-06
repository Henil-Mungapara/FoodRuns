package com.example.food_runs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Privacy_Page extends AppCompatActivity {

    private CheckBox checkBoxTerms, checkBoxPrivacy;
    private Button btnContinue;
    private boolean isNavigated = false; // Prevents multiple intents

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy_page);

        // Initialize Views
        checkBoxTerms = findViewById(R.id.checkBoxTerms);
        checkBoxPrivacy = findViewById(R.id.checkBoxPrivacy);
        btnContinue = findViewById(R.id.btnContinue);

        // Initially disable button and set alpha
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        // Checkbox change listeners
        checkBoxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonState());
        checkBoxPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonState());

        // Continue button click event
        btnContinue.setOnClickListener(v -> {
            if (checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked() && !isNavigated) {
                isNavigated = true; // Prevents multiple clicks
                Intent iNext = new Intent(getApplicationContext(), SignUp_Page.class);
                startActivity(iNext);
                finish();
            }
        });
    }

    private void updateButtonState() {
        // Enable button only if both checkboxes are checked
        if (checkBoxTerms.isChecked() && checkBoxPrivacy.isChecked()) {
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1f);
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setAlpha(0.5f);
        }
    }
}
