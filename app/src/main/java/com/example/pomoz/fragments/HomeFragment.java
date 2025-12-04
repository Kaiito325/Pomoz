package com.example.pomoz.fragments;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ListenableWorker;

import com.example.pomoz.R;
import com.example.pomoz.activities.TaskInfoActivity;
import com.example.pomoz.adapters.NotificationAdapter;
import com.example.pomoz.adapters.TaskAdapter;
import com.example.pomoz.data.db_config.ApiClient;
import com.example.pomoz.model_classes.Action;
import com.example.pomoz.model_classes.Notification;
import com.example.pomoz.model_classes.Task;
import com.example.pomoz.views.SemiCircleProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    private TextView userName;
    private TextView uniquePersons, tasksDone;
    private TextView totalPersons, totalTokens, totalTasks;
    private RecyclerView messageLayout;
    private TextView messageHeader, messageContent;
    private RecyclerView availableTasks;
//    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private SemiCircleProgressView progressView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        progressView= v.findViewById(R.id.semiProgress);
        uniquePersons = v.findViewById(R.id.uniquePersons);
        tasksDone = v.findViewById(R.id.tasksDone);
        availableTasks = v.findViewById(R.id.availableTasks);
        totalPersons = v.findViewById(R.id.totalUsers);
        totalTasks = v.findViewById(R.id.totalTasks);
        totalTokens = v.findViewById(R.id.totalTokens);
        userName = v.findViewById(R.id.userName);
        userName.setText(ApiClient.getInstance(getContext()).getUserName());

        messageLayout = v.findViewById(R.id.messageInfo);

        messageLayout.setLayoutManager(new LinearLayoutManager(getContext()));


        SwipeRefreshLayout swipeRefresh = v.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            loadTasks(swipeRefresh);
            checkNotifications(swipeRefresh);
        });


        startStatsAnimation(progressView);
        loadTasks(swipeRefresh);
        checkNotifications(swipeRefresh);

        availableTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(new ArrayList<>(),task -> {
            Intent i = new Intent(getContext(), TaskInfoActivity.class);
            i.putExtra("task", task);
            startActivity(i);
        }); // pusty adapter na start
        availableTasks.setAdapter(taskAdapter);

        Fragment calendarFragment = new CalendarFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.calendarContainer, calendarFragment)
                .commit();


        return v;
    }
    private void loadTasks(SwipeRefreshLayout refresher) {

        new Thread(() -> {
            try {
                Response response = ApiClient.getInstance(getContext()).post("get_actual_tasks");
                Context context = getContext();

                if (response.isSuccessful() && response.body() != null) {
                    String bodyStr = response.body().string();
                    JSONArray tasks = new JSONArray(bodyStr);

                    List<Task> downloaded = new ArrayList<>();
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);
//                        Log.d(TAG, "loadTasks: " + task.toString());
                        downloaded.add(new Task(
                                Integer.parseInt(task.getString("id")),
                                Integer.parseInt(task.getString("id_potrzebujacy")),
                                Integer.parseInt(task.getString("sumaPkt")),
                                Integer.parseInt(task.getString("czynnosc")),
                                task.getString("nazwa"),
                                task.getString("img_id"),
                                task.getString("opis"),
                                task.getString("czas"),
                                task.getString("termin"),
                                task.getString("trudnosc"),
                                task.getString("miejscowosc"),
                                context
                        ));
                    }

                    requireActivity().runOnUiThread(() -> {
                        taskAdapter.updateTasks(downloaded);
                        Toast.makeText(getContext(), "Odświeżono", Toast.LENGTH_SHORT).show();
                        startStatsAnimation(progressView);
                        refresher.setRefreshing(false);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Błąd pobierania", Toast.LENGTH_SHORT).show();
                        refresher.setRefreshing(false);
                    });
                }
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "loadTasks: " + e);
                    refresher.setRefreshing(false);
                });
            }
        }).start();
    }
    private void startStatsAnimation(SemiCircleProgressView progressView) {

        int currentStep = 200;
        int maxStep = 500;

        int tasksMax = 3;
        int uniquePersonsMax = 2;

        int totalTasksMax = 52;
        int totalUniquePersonsMax = 23;
        int totalTokensMax = 6500;

        ValueAnimator animation = ValueAnimator.ofInt(0, currentStep);
        animation.setDuration(1500);
        animation.addUpdateListener(animator -> {
            int value = (int) animator.getAnimatedValue();
            progressView.setTokenDrawable(R.drawable.honor_token);
            progressView.setProgressSteps(value, maxStep);

            float progressFraction = value / (float) currentStep;

            tasksDone.setText(String.valueOf((int) (progressFraction * tasksMax)));
            uniquePersons.setText(String.valueOf((int) (progressFraction * uniquePersonsMax)));
            totalTasks.setText(String.valueOf((int) (progressFraction * totalTasksMax)));
            totalPersons.setText(String.valueOf((int) (progressFraction * totalUniquePersonsMax)));
            totalTokens.setText(String.valueOf((int) (progressFraction * totalTokensMax)));
        });
        animation.start();
    }

    public void checkNotifications(SwipeRefreshLayout refresher){
        new Thread(() -> {
            Context context = getContext();
            int userId = ApiClient.getInstance(context).getUserId();

            try {
                Map<String, String> data = new HashMap<>();
                data.put("user_id", String.valueOf(userId));

                Response response = ApiClient.getInstance(context)
                        .get("get_notifications", data);
                Log.d(TAG, "checkNotifications: " + response);

                if (response.isSuccessful() && response.body() != null) {

                    String bodyStr = response.body().string();
                    JSONArray notifications = new JSONArray(bodyStr);

                    Handler main = new Handler(Looper.getMainLooper());

                    main.post(() -> {
                        messageLayout.setVisibility(VISIBLE);
                        RecyclerView recycler = requireView().findViewById(R.id.messageInfo);
                        recycler.setLayoutManager(new LinearLayoutManager(context));

                        List<Notification> list = new ArrayList<>();

                        for (int i = 0; i < notifications.length(); i++) {
                            try {
                                JSONObject n = notifications.getJSONObject(i);
                                Log.d(TAG, "checkNotifications: " + n);

                                list.add(new Notification(
                                        n.getInt("id"),
                                        n.getInt("user_id"),
                                        n.getString("title"),
                                        n.getString("body")
                                ));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        recycler.setAdapter(new NotificationAdapter(list));

                        if (refresher != null)
                            refresher.setRefreshing(false);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}
