package com.example.pomoz.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomoz.R;
import com.example.pomoz.adapters.TaskAdapter;
import com.example.pomoz.model_classes.Task;
import com.example.pomoz.views.SemiCircleProgressView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public HomeFragment() {}
    private TextView uniquePersons, tasksDone;
    private TextView totalPersons, totalTokens, totalTasks;
    private RecyclerView availableTasks;
    private List<Task> tasks = new ArrayList<>();
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

        tasks.add(new Task("Koszenie trawy", "Powierzchnia 6-10a", "Wodzisław Śląski", 150));
        tasks.add(new Task("Zrobić zakupy", "Ilość przedmiotów: 5-10", "Wodzisław Śląski", 20));
        taskAdapter = new TaskAdapter(tasks);
        availableTasks.setAdapter(taskAdapter);

        Fragment calendarFragment = new CalendarFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.calendarContainer, calendarFragment)
                .commit();


        return v;
    }
}
