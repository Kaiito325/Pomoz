package com.example.pomoz.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.pomoz.activities.LoginActivity;
import com.example.pomoz.R;
import com.example.pomoz.adapters.MainPagerAdapter;
import com.example.pomoz.data.NotificationWorker;
import com.example.pomoz.data.db_config.ApiClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ApiClient.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager2);

        viewPager.setAdapter(new MainPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_add);
                    break;
                case 2:
                    tab.setIcon(R.drawable.ic_account);
                    break;
            }
        }).attach();
        Log.d("WORK_TEST", "Worker enqueued");
        checkNotificationsEnabled();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "Powiadomienia",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

        if (enabled) {
            startWorker();
        }


    }
    private void startWorker() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notification_polling",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
        );
    }

    private void checkNotificationsEnabled() {
        boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

        if (!enabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Włącz powiadomienia")
                    .setMessage("Aby otrzymywać powiadomienia, włącz je w ustawieniach systemowych.")
                    .setPositiveButton("Przejdź do ustawień", (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                        startActivity(intent);
                    })
                    .setNegativeButton("Anuluj", null)
                    .show();
        } else {
            // Powiadomienia są włączone → uruchamiamy worker
            startWorker();
        }
    }


}
