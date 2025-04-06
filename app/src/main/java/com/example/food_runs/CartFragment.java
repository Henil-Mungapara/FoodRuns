package com.example.food_runs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class CartFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout and store in a View variable
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize Toolbar
        Toolbar toolbar = view.findViewById(R.id.carttoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // Handle Back Arrow Click
            ImageView backArrow = view.findViewById(R.id.backArrow);
            if (backArrow != null) {
                backArrow.setOnClickListener(v -> {
                    // Clear fragment stack
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    // Load CartFragment again (optional, if meant to refresh)
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new CategoryFragment())
                            .commit();

                    // Update BottomNavigationView selection
                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_categories);
                    }
                });
            }
        }

        return view; // ✅ Return after setting everything up
    }
}
