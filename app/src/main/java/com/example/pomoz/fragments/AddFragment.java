package com.example.pomoz.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.example.pomoz.R;
import com.example.pomoz.adapters.ActionAdapter;
import com.example.pomoz.model_classes.Action;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddFragment extends Fragment {

    public AddFragment() {
    }
    private EditText inputTerm, inputAction;
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.add_fragment, container, false);


        inputTerm = v.findViewById(R.id.input_due_date);
        inputAction= v.findViewById(R.id.input_action);


        setupTermPicker();
        setupActionDialog();

        return v;
    }

    private void setupTermPicker () {
        inputTerm.setOnClickListener(v -> {


            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now());

            MaterialDatePicker<Long> picker =
                    MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Wybierz termin")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .setCalendarConstraints(constraintsBuilder.build())
                            .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                inputTerm.setText(picker.getHeaderText());
            });

            picker.show(getParentFragmentManager(), "DATE_PICKER");
        });
    }


    private void setupActionDialog() {
        inputAction.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.action_dialog, null);
            builder.setView(dialogView);

            EditText search = dialogView.findViewById(R.id.searchEditText);
            RecyclerView recyclerView = dialogView.findViewById(R.id.actionRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Action> lista = new ArrayList<>();
            lista.add(new Action("Koszenie trawy"));
            lista.add(new Action("Malowanie ścian"));
            lista.add(new Action("Sprzątanie hali"));
            lista.add(new Action("Mycie okien"));
            lista.add(new Action("Naprawa sprzętu"));
            lista.add(new Action("Pomoc przy komputerze"));

            AlertDialog dialog = builder.create();
            ActionAdapter adapter = new ActionAdapter(lista, a -> {
                inputAction.setText(a.getName());
                dialog.dismiss();
            });
            recyclerView.setAdapter(adapter);

            search.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }
                @Override public void afterTextChanged(Editable s) {}
            });

            dialog.show();
        });
    }
}