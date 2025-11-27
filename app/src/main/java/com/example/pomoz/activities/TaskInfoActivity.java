package com.example.pomoz.activities;

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

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class TaskInfoActivity extends AppCompatActivity {
    private Button takeTaskBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_info_activity);
        takeTaskBtn = findViewById(R.id.takeTaskBtn);
        Task task = (Task) getIntent().getSerializableExtra("task");

        if (task != null) {
            TextView name = findViewById(R.id.taskName);
            TextView desc = findViewById(R.id.taskDesc);
            TextView loc = findViewById(R.id.taskLoc);
            TextView tok = findViewById(R.id.taskTokens);

            name.setText(task.getName());
            desc.setText(task.getDescription());
            loc.setText(task.getLocation());
            tok.setText(String.valueOf(task.getTokens()));
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
