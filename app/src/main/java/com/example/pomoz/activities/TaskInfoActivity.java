package com.example.pomoz.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pomoz.R;
import com.example.pomoz.data.db_config.ApiClient;
import com.example.pomoz.model_classes.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class TaskInfoActivity extends AppCompatActivity {
    private Button takeTaskBtn, sendMessageBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_activity);
        takeTaskBtn = findViewById(R.id.takeTaskBtn);
        sendMessageBtn = findViewById(R.id.sendMessage);
        Task task = (Task) getIntent().getSerializableExtra("task");

        if (task != null) {
            TextView name = findViewById(R.id.taskName);
            TextView desc = findViewById(R.id.taskDesc);
            TextView loc = findViewById(R.id.taskLoc);
            TextView tok = findViewById(R.id.taskTokens);
            TextView userName = findViewById(R.id.userName);
            TextView userId = findViewById(R.id.userId);

            name.setText(task.getName());
            desc.setText(task.getDescription());
            loc.setText(task.getLocation());
            tok.setText(String.valueOf(task.getTokens()));

            new Thread(() -> {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("userId", task.getUserId() + "");

                    Response response = ApiClient.getInstance(this).post("get_user_name", data);

                    if (response.isSuccessful() && response.body() != null) {
                        String bodyStr = response.body().string();

                        runOnUiThread(() -> {
                            try {
                                JSONObject obj = new JSONObject(bodyStr);
                                userId.setText(obj.getString("id"));
                                userName.setText(obj.getString("imie"));

                                Log.d(TAG, "User: " + obj.getString("id") + " " + obj.getString("imie"));

                            } catch (Exception e) {
                                Log.e(TAG, "JSON parse error", e);
                            }
                        });

                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Błąd pobierania", Toast.LENGTH_SHORT).show()
                        );
                    }

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Błąd połączenia", Toast.LENGTH_SHORT).show()
                    );
                    Log.e(TAG, "Connection error", e);
                }
            }).start();
        }


            takeTaskBtn.setOnClickListener(v -> {
            int taskId = task.getId(); // ID zadania
            int userId = ApiClient.getInstance(this).getUserId();

            new Thread(() -> {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", String.valueOf(userId));
                    data.put("task_id", String.valueOf(taskId));
                    Log.d("ACCEPT_TASK", "User ID: " + userId + ", Task ID: " + taskId);


                    // Wywołanie POST i odczyt odpowiedzi
                    Response response = ApiClient.getInstance(this).post("accept_task", data);
                    String bodyStr = response.body() != null ? response.body().string() : "Brak odpowiedzi";

                    // Logujemy odpowiedź
                    Log.d("ACCEPT_TASK", "Response code: " + response.code() + ", body: " + bodyStr);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Toast.makeText(this, "Zadanie przyjęte", Toast.LENGTH_SHORT).show();
        });


    }
}
