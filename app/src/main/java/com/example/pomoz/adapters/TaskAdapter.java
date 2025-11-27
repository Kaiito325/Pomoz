package com.example.pomoz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomoz.R;
import com.example.pomoz.model_classes.Action;
import com.example.pomoz.model_classes.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskClickListener listener;
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskName, taskDescription, taskLocation, taskTokens;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDesc);
            taskLocation = itemView.findViewById(R.id.taskLoc);
            taskTokens = itemView.findViewById(R.id.taskTokens);
        }
        public void bind(Task task, OnTaskClickListener listener) {
            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
    public void updateTasks(List<Task> newTasks) {
        taskList.clear();
        taskList.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getName());
        holder.taskDescription.setText(task.getDescription());
        holder.taskLocation.setText(task.getLocation());
        holder.taskTokens.setText(task.getTokens() + "");
        holder.bind(task,listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
