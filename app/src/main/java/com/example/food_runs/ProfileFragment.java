package com.example.food_runs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private Button btnSignOut, btnEdit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private TextView tvName, tvEmail, tvMobile;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_OUT = "isLoggedOut";

    private static final int EDIT_PROFILE_REQUEST_CODE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMobile = view.findViewById(R.id.tvMobile);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnEdit = view.findViewById(R.id.btnEdit);

        // Load user info
        loadUserProfile();

        btnSignOut.setOnClickListener(v -> signOutUser());

        btnEdit.setOnClickListener(v -> {
            // Start the Edit_Profile activity with a request code
            Intent iNext = new Intent(getContext(), Edit_Profile.class);
            startActivityForResult(iNext, EDIT_PROFILE_REQUEST_CODE);
        });

        // Toolbar setup
        Toolbar toolbar = view.findViewById(R.id.profiletoolbar);
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
                            .replace(R.id.frame_container, new CartFragment())
                            .commit();

                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.nav_cart);
                    }
                });
            }
        }
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
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

                        tvName.setText(name != null ? name : "N/A");
                        tvEmail.setText(email != null ? email : "N/A");
                        tvMobile.setText(mobile != null ? mobile : "N/A");
                    } else {
                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void signOutUser() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Signing out...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signOut();

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_LOGGED_OUT, true).apply();

        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), LogIn_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }, 1500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // After returning from Edit_Profile, reload the profile data
            loadUserProfile();
        }
    }
}
