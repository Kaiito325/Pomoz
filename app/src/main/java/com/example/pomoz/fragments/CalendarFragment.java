package com.example.pomoz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pomoz.R;
import com.example.pomoz.adapters.CalendarAdapter;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private RecyclerView calendarRecycler;
    private TextView monthLabel;
    private LocalDate currentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_fragment, container, false);

        calendarRecycler = v.findViewById(R.id.calendarRecycler);
        monthLabel = v.findViewById(R.id.monthLabel);
        ImageView prevBtn = v.findViewById(R.id.prevMonthBtn);
        ImageView nextBtn = v.findViewById(R.id.nextMonthBtn);

        calendarRecycler.setLayoutManager(new GridLayoutManager(getContext(), 7));
        currentDate = LocalDate.now();

        updateCalendar();

        prevBtn.setOnClickListener(view -> changeMonth(-1));
        nextBtn.setOnClickListener(view -> changeMonth(1));

        return v;
    }

    private void updateCalendar() {
        List<String> days = generateMonthDays(currentDate);
        int today = (currentDate.getMonthValue() == LocalDate.now().getMonthValue()
                && currentDate.getYear() == LocalDate.now().getYear())
                ? LocalDate.now().getDayOfMonth() : -1;

        CalendarAdapter adapter = new CalendarAdapter(getContext(), days, today);
        calendarRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // 👈 wymuś odświeżenie

        String monthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl"));
        monthLabel.setText(
                monthName.substring(0, 1).toUpperCase() + monthName.substring(1) + " " + currentDate.getYear()
        );
    }


    private List<String> generateMonthDays(LocalDate date) {
        List<String> days = new ArrayList<>();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = poniedziałek
        int daysInMonth = date.lengthOfMonth();

        // Puste przed pierwszym dniem
        for (int i = 1; i < startDayOfWeek; i++) {
            days.add("");
        }

        // Faktyczne dni miesiąca
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(String.valueOf(i));
        }

        // Uzupełniamy puste na końcu, żeby kalendarz miał pełne rzędy
        while (days.size() % 7 != 0) {
            days.add("");
        }

        return days;
    }


    private void changeMonth(int offset) {
        currentDate = currentDate.plusMonths(offset);

        // animacja przesuwania
        if (offset > 0) {
            calendarRecycler.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        } else {
            calendarRecycler.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
        }

        updateCalendar();
    }
}
