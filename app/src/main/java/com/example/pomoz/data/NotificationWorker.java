package com.example.pomoz.data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pomoz.R;
import com.example.pomoz.data.db_config.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        int userId = ApiClient.getInstance(context).getUserId();

        if (userId == -1) return Result.failure();

        try {
            Map<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(userId));

            // synchroniczne wywołanie
            Response response = ApiClient.getInstance(context).get("get_notifications", params);

            if (!response.isSuccessful() || response.body() == null) return Result.failure();

            String body = response.body().string();
            JSONArray notifications = new JSONArray(body);
            Log.d("NotificationWorker", "Notifications length: " + notifications.length());

            for (int i = 0; i < notifications.length(); i++) {
                JSONObject n = notifications.getJSONObject(i);
                showNotification(context, n.getString("title"), n.getString("body"));
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }


    private void showNotification(Context context, String title, String body) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.honor_token)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}