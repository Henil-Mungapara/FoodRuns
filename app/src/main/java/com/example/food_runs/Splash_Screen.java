package com.example.food_runs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Splash_Screen extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String KEY_IS_FIRST_INSTALL = "isFirstInstall";
    public static final String KEY_IS_LOGGED_OUT = "isLoggedOut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean isFirstInstall = prefs.getBoolean(KEY_IS_FIRST_INSTALL, true);
            boolean isLoggedOut = prefs.getBoolean(KEY_IS_LOGGED_OUT, false);
            //boolean isLoggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;

            Intent intent;

            if (isFirstInstall) {
                // Show Welcome Notification
                //showWelcomeNotification();

                // Go to Get Started page
                intent = new Intent(Splash_Screen.this, Get_Started.class);
                prefs.edit().putBoolean(KEY_IS_FIRST_INSTALL, false).apply();

            } else if (isLoggedOut) {
                intent = new Intent(Splash_Screen.this, LogIn_Page.class);
            } else {
                intent = new Intent(Splash_Screen.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }, 4500); // 4.5 seconds splash screen
    }

}
