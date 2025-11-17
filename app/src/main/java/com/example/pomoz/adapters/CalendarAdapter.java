package com.example.pomoz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pomoz.R;
import java.time.LocalDate;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {
    private final List<String> days;
    private final Context context;
    private final int currentDay;

    public CalendarAdapter(Context context, List<String> days, int currentDay) {
        this.context = context;
        this.days = days;
        this.currentDay = currentDay;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        String day = days.get(position);
        holder.dayText.setText(day);

        if (day.isEmpty()) {
            holder.dayText.setVisibility(View.INVISIBLE);
        } else {
            holder.dayText.setVisibility(View.VISIBLE);
        }

        if (!day.isEmpty() && Integer.parseInt(day) == currentDay) {
            holder.dayText.setBackgroundResource(R.drawable.day_bg_highlight);
        } else {
            holder.dayText.setBackgroundResource(R.drawable.day_bg);
        }
    }


    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
