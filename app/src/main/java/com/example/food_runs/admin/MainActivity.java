package com.example.food_runs.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.food_runs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView adminNav;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        adminNav = findViewById(R.id.admin_bottom_navigation);

        adminNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_users) {
                loadFragment(new UsersManageFragment());
            } else if (id == R.id.nav_add) {
                loadFragment(new AddItemFragment());
            } else if (id == R.id.nav_admin_profile) {
                loadFragment(new AdminProfileFragment());
            }

            return true;
        });

        adminNav.setSelectedItemId(R.id.nav_users); // default fragment
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.admin_frame_container, fragment);
        ft.commit();
    }
}
