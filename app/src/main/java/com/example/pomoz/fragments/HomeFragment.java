package com.example.pomoz.fragments;

import static android.content.ContentValues.TAG;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pomoz.R;
import com.example.pomoz.activities.TaskInfoActivity;
import com.example.pomoz.adapters.TaskAdapter;
import com.example.pomoz.data.db_config.ApiClient;
import com.example.pomoz.model_classes.Action;
import com.example.pomoz.model_classes.Task;
import com.example.pomoz.views.SemiCircleProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    private TextView userName;
    private TextView uniquePersons, tasksDone;
    private TextView totalPersons, totalTokens, totalTasks;
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

        SwipeRefreshLayout swipeRefresh = v.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            loadTasks(swipeRefresh);
        });


        startStatsAnimation(progressView);
        loadTasks(swipeRefresh);


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


}
