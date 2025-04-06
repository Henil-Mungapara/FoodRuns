package com.example.food_runs;

import android.annotation.SuppressLint;
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


public class CategoryFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the correct layout
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize Toolbar
        Toolbar toolbar = view.findViewById(R.id.categoriestoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // Back Button Handling
            ImageView backArrow = view.findViewById(R.id.backArrow);
            if (backArrow != null) {
                backArrow.setOnClickListener(v -> {
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new HomeFragment())
                            .commit();

                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_home);
                    }
                });
            }
        }

        return view;
    }


}