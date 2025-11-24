package com.example.pomoz.fragments;

import android.animation.ValueAnimator;
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

import com.example.pomoz.R;
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
    private TextView uniquePersons, tasksDone;
    private TextView totalPersons, totalTokens, totalTasks;
    private RecyclerView availableTasks;
//    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.home_fragment, container, false);
        SemiCircleProgressView progressView = v.findViewById(R.id.semiProgress);
        uniquePersons = v.findViewById(R.id.uniquePersons);
        tasksDone = v.findViewById(R.id.tasksDone);
        availableTasks = v.findViewById(R.id.availableTasks);
        totalPersons = v.findViewById(R.id.totalUsers);
        totalTasks = v.findViewById(R.id.totalTasks);
        totalTokens = v.findViewById(R.id.totalTokens);

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


        availableTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(new ArrayList<>()); // pusty adapter na start
        availableTasks.setAdapter(taskAdapter);


        new Thread(() ->{
            try{
                Response response = ApiClient.getInstance(getContext()).post("get_actual_tasks");
                Log.d("TAG", "onCreateView: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    String bodyStr = response.body().string();
                    JSONArray tasks = new JSONArray(bodyStr);

                    List<Task> downloaded = new ArrayList<>();
                    for (int i = 0; i < tasks.length(); i++) {
                        JSONObject task = tasks.getJSONObject(i);
                        Log.d("TAG", "task: " + task);
                        downloaded.add(new Task(task.getString("nazwa"), task.getString("opis"), task.getString("miejscowosc"), Integer.parseInt(task.getString("sumaPkt"))));
                    }

                    // Aktualizacja UI w wątku głównym
                    requireActivity().runOnUiThread(() -> {
                        taskAdapter.updateTasks(downloaded);
                        Toast.makeText(getContext(), "Pobrano " + downloaded.size() + " zadań", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Błąd pobierania zadań", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();


        Fragment calendarFragment = new CalendarFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.calendarContainer, calendarFragment)
                .commit();


        return v;
    }

}
