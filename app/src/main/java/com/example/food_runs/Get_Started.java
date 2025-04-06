package com.example.food_runs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;

public class Get_Started extends AppCompatActivity {
    Button started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);

        started = findViewById(R.id.btnGetStarted);

        showWelcomeNotification(); // 🔔 Show notification here

        started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iNext = new Intent(getApplicationContext(), Privacy_Page.class);
                startActivity(iNext);
                finish();
            }
        });
    }

    private void showWelcomeNotification() {
        String channelId = "welcome_channel";
        String channelName = "Welcome Notifications";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Welcome message when first opening the app");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("👋 Welcome to Food Runs!")
                .setContentText("Get started with some delicious experiences.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(100, builder.build());
    }
}
