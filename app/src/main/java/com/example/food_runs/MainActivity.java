package com.example.food_runs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bnview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bnview = findViewById(R.id.bottom_navigation);


        bnview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if(id==R.id.nav_home){
                    loadfragment(new HomeFragment(),true);
                }
                else if(id==R.id.nav_item){
                    loadfragment(new FoodStoreFragment(),false);
                }
                /*else if(id==R.id.nav_categories){
                    loadfragment(new CategoryFragment(),false);
                }*/
                else if(id==R.id.nav_cart){
                    loadfragment(new CartFragment(),false);
                }
                else{
                    loadfragment(new ProfileFragment(),false);
                }

                return true;
            }
        });
        bnview.setSelectedItemId(R.id.nav_home);

    }

    public void loadfragment(Fragment fragment, Boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag){
            ft.add(R.id.frame_container,fragment);
        }
        else{
            ft.replace(R.id.frame_container,fragment);
        }
        ft.commit();
    }



}