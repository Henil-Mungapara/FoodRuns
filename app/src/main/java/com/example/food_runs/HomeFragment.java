package com.example.food_runs;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Button click listeners
        rootView.findViewById(R.id.btnBuyNow).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FoodStoreFragment.class)));

        rootView.findViewById(R.id.btnAdd1).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FoodStoreFragment.class)));

        rootView.findViewById(R.id.btnAdd2).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FoodStoreFragment.class)));

        rootView.findViewById(R.id.card1).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FoodStoreFragment.class)));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showExitDialog();
                    }
                }
        );
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setIcon(R.drawable.ic_exit);
        builder.setTitle("Exit App");
        builder.setMessage("Do you really want to exit?");

        builder.setPositiveButton("Yes, Exit", (dialog, which) -> requireActivity().finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
