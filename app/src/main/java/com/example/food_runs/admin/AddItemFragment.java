package com.example.food_runs.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.food_runs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddItemFragment extends Fragment {

    private EditText etTitle, etDesc, etPrice, etImageUrl;
    private Spinner spinnerCategory;
    private RadioGroup radioGroup;
    private Button btnSubmit;

    private FirebaseFirestore db;

    private String[] categories = {
            "Pizza", "Burger", "Biryani", "Pasta", "Ice Cream", "Beverages", "Wraps", "Noodles",
            "Fries", "Dessert", "Soup", "Grill", "Salad", "Tandoori", "Sandwich"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        Toolbar toolbar = view.findViewById(R.id.additemtoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView backArrow = view.findViewById(R.id.backArrow);
            if (backArrow != null) {
                backArrow.setOnClickListener(v -> {
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new UsersManageFragment())
                            .commit();

                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_users);
                    }
                });
            }
        }


            etTitle = view.findViewById(R.id.etItemTitle);
        etDesc = view.findViewById(R.id.etItemDesc);
        etPrice = view.findViewById(R.id.etItemPrice);
        etImageUrl = view.findViewById(R.id.etImageUrl);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        radioGroup = view.findViewById(R.id.radioGroupAvailability);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Submit Button Click
        btnSubmit.setOnClickListener(v -> submitItem());

        return view;
    }

    private void submitItem() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String availability = getSelectedAvailability();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(price) || TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(availability)) {
            Snackbar.make(getView(), "Please fill all fields", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("description", desc);
        item.put("price", price);
        item.put("imageUrl", imageUrl);
        item.put("category", category);
        item.put("availability", availability);

        db.collection("MenuItems")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Snackbar.make(getView(), "Item added successfully!", Snackbar.LENGTH_LONG).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Snackbar.make(getView(), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG).show());
    }

    private String getSelectedAvailability() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selected = getView().findViewById(selectedId);
            return selected.getText().toString();
        }
        return "";
    }

    private void clearFields() {
        etTitle.setText("");
        etDesc.setText("");
        etPrice.setText("");
        etImageUrl.setText("");
        radioGroup.clearCheck();
        spinnerCategory.setSelection(0);
    }
}
