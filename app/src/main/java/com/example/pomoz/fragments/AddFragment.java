package com.example.pomoz.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.pomoz.R;
import com.example.pomoz.activities.LoginActivity;
import com.example.pomoz.activities.MainActivity;
import com.example.pomoz.adapters.ActionAdapter;
import com.example.pomoz.data.db_config.ApiClient;
import com.example.pomoz.model_classes.Action;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

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

            AlertDialog dialog = builder.create();
            dialog.show();

            List<Action> lista = new ArrayList<>();
            ActionAdapter adapter = new ActionAdapter(lista, a -> {
                inputAction.setText(a.getName());
                dialog.dismiss();
            });
            recyclerView.setAdapter(adapter);

            new Thread(() -> {
                try {
                    Response response = ApiClient.getInstance(getContext())
                            .post("get_tasks");

                    if (response.isSuccessful() && response.body() != null) {
                        String bodyStr = response.body().string();
                        JSONArray tasks = new JSONArray(bodyStr);

                        List<Action> downloaded = new ArrayList<>();
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject task = tasks.getJSONObject(i);
                            downloaded.add(new Action(task.getString("nazwa")));
                        }

                        // Aktualizacja UI w wątku głównym
                        requireActivity().runOnUiThread(() -> {
                            adapter.updateList(downloaded);
                            Toast.makeText(getContext(), "Pobrano " + downloaded.size() + " zadań", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Błąd pobierania zadań", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Błąd sieci lub parsowania JSON", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();


            search.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        });
    }

}