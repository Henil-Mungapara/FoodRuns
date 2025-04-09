package com.example.food_runs.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.food_runs.CartFragment;
import com.example.food_runs.LogIn_Page;
import com.example.food_runs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminProfileFragment extends Fragment {

    private Button btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        // Toolbar setup
        Toolbar toolbar = view.findViewById(R.id.adminprofiletoolbar);
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            ImageView backArrow = view.findViewById(R.id.backArrow);
            backArrow.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.admin_frame_container, new ManageItemFragment())
                        .commit();

                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.admin_bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_manage);
                }
            });
        }


            // Bind Sign Out button
        btnSignOut = view.findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(v -> {
            // Show ProgressDialog
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Signing out...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Delay to simulate sign out process
            new Handler().postDelayed(() -> {
                progressDialog.dismiss();

                // Go to LoginActivity
                Intent intent = new Intent(getActivity(), LogIn_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();

            }, 2000); // 2 seconds delay
        });

        return view;
    }
}
