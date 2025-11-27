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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pomoz.R;
import com.example.pomoz.activities.LoginActivity;
import com.example.pomoz.activities.MainActivity;
import com.example.pomoz.adapters.ActionAdapter;
import com.example.pomoz.adapters.StarAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class AddFragment extends Fragment {
    private ProgressBar progressBar;
    private EditText inputTaskName, inputDescription, inputArea, inputQuantity,
            inputTaskTime, inputTotalPoints;
    private Action selectedAction;
    private Spinner spinnerDifficulty;


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
        inputTaskName = v.findViewById(R.id.input_task_name);
        inputDescription = v.findViewById(R.id.input_description);
        inputArea = v.findViewById(R.id.input_area);
        inputQuantity = v.findViewById(R.id.input_quantity);
        inputTaskTime = v.findViewById(R.id.input_task_time);


        inputTotalPoints = v.findViewById(R.id.input_total_points);

        Button btnAdd = v.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view -> sendTask());
        Button btnCancel = v.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(view -> clear());
        spinnerDifficulty = v.findViewById(R.id.spinnerDifficulty);

        List<Integer> stars = new ArrayList<>();
        for (int i = 1; i <= 5; i++) stars.add(i);

        StarAdapter adapter = new StarAdapter(getContext(), stars);
        spinnerDifficulty.setAdapter(adapter);

        setupTermPicker();
        setupActionDialog();

        return v;
    }

    private void setupTermPicker() {
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
                // selection = timestamp (Long)
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1; // 0–11, więc +1
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                String formatted = String.format("%04d-%02d-%02d", year, month, day);
                inputTerm.setText(formatted);
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
            progressBar = dialogView.findViewById(R.id.loadingProgressBar);
            AlertDialog dialog = builder.create();
            dialog.show();

            List<Action> lista = new ArrayList<>();
            ActionAdapter adapter = new ActionAdapter(lista, a -> {
                selectedAction = a;
                inputAction.setText(a.getName());
                switch (a.getType()) {
                    case "powierzchnia":
                        inputArea.setVisibility(View.VISIBLE);
                        inputQuantity.setVisibility(View.GONE);
                        break;
                    case "ilosc":
                        inputQuantity.setVisibility(View.VISIBLE);
                        inputArea.setVisibility(View.GONE);
                        break;
                    default:
                        inputArea.setVisibility(View.GONE);
                        inputQuantity.setVisibility(View.GONE);
                        break;
                }
                dialog.dismiss();
            });
            recyclerView.setAdapter(adapter);

            new Thread(() -> {
                requireActivity().runOnUiThread(() ->
                        progressBar.setVisibility(View.VISIBLE)
                );
                try {
                    Response response = ApiClient.getInstance(getContext())
                            .post("get_tasks");

                    if (response.isSuccessful() && response.body() != null) {
                        String bodyStr = response.body().string();
                        JSONArray tasks = new JSONArray(bodyStr);

                        List<Action> downloaded = new ArrayList<>();
                        for (int i = 0; i < tasks.length(); i++) {
                            JSONObject task = tasks.getJSONObject(i);
                            downloaded.add(new Action(task.getString("nazwa"), Float.parseFloat(task.getString("mnoznik")), task.getString("typ"), task.getString("img_id"), getContext()));
                        }

                        // Aktualizacja UI w wątku głównym
                        requireActivity().runOnUiThread(() -> {
                            adapter.updateList(downloaded);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Pobrano " + downloaded.size() + " zadań", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Błąd pobierania zadań", Toast.LENGTH_SHORT).show();
                        });
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

    private void sendTask() {

        // Pobranie danych z pól
        String taskName = inputTaskName.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String action = inputAction.getText().toString().trim();
        String area = inputArea.getText().toString().trim();
        String quantity = inputQuantity.getText().toString().trim();
        String taskTime = inputTaskTime.getText().toString().trim();
        String dueDate = inputTerm.getText().toString().trim();
        int difficulty = (int) spinnerDifficulty.getSelectedItem();
        float totalPoints;

        if(selectedAction != null){
            totalPoints = calculatePoints(selectedAction, area, quantity, difficulty + "");
        } else {
            totalPoints = 0;
        }

        // Walidacja
        if (taskName.isEmpty()) {
            Toast.makeText(getContext(), "Podaj nazwę zadania", Toast.LENGTH_SHORT).show();
            return;
        }


        // WYSYŁANIE W NOWYM WĄTKU
        new Thread(() -> {

            requireActivity().runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

            try {
                // Tworzenie JSON do wysyłki
                Map<String, String> data = new HashMap<>();
                data.put("name", taskName);
                data.put("description", description);
                data.put("task_action", action);
                data.put("area", area);
                data.put("quantity", quantity);
                data.put("task_time", taskTime);
                data.put("due_date", dueDate);
                data.put("difficulty", difficulty + "");
                data.put("total_points", totalPoints + "");

                Response response = ApiClient.getInstance(getContext())
                        .post("add_task", data);

                Log.d("TAG", "sendTask: "  + data.toString());

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        try {
                            String responseBody = response.body() != null ? response.body().string() : "";
                            Log.d("TAG", "RAW RESPONSE: " + responseBody);
                            JSONObject json = new JSONObject(responseBody);

                            if (json.has("error")) {
                                Toast.makeText(getContext(), "Błąd: " + json.getString("error"), Toast.LENGTH_LONG).show();
                            } else if (json.has("success") && json.getBoolean("success")) {
                                Toast.makeText(getContext(), "Zadanie dodane! ID: " + json.optInt("task_id"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Nieoczekiwana odpowiedź: " + responseBody, Toast.LENGTH_LONG).show();
                            }
                            Log.d("TAG", "sendTask: " + responseBody);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Błąd parsowania odpowiedzi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Brak odpowiedzi z serwera", Toast.LENGTH_SHORT).show();
                    }


                });

            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Błąd sieci", Toast.LENGTH_SHORT).show();
                });
            }

        }).start();
    }
    private void clear() {
        inputTaskName.setText("");
        inputDescription.setText("");
        inputAction.setText("");
        inputArea.setText("");
        inputQuantity.setText("");
        inputTaskTime.setText("");
        inputTerm.setText("");
        spinnerDifficulty.setSelection(0);
        inputTotalPoints.setText("");
    }
    private float calculatePoints(Action action, String areaStr, String quantityStr, String difficultyStr) {

        float baseMultiplier = action.getMultiplier(); // mnożnik z obiektu Action
        float value = 0;

        // ilość lub metr kw.
        if (action.getType().equals("powierzchnia")) {
            value = areaStr.isEmpty() ? 0 : Float.parseFloat(areaStr);
        } else if (action.getType().equals("ilosc")) {
            value = quantityStr.isEmpty() ? 0 : Float.parseFloat(quantityStr);
        }

        // trudność
        int diff = difficultyStr.isEmpty() ? 1 : Integer.parseInt(difficultyStr);

        float difficultyMultiplier = 1f;

        switch (diff) {
            case 1: difficultyMultiplier = 1f; break;
            case 2: difficultyMultiplier = 1.2f; break;
            case 3: difficultyMultiplier = 1.5f; break;
            case 4: difficultyMultiplier = 2.0f; break;
            case 5: difficultyMultiplier = 3.0f; break;
        }
        float points = value * baseMultiplier * difficultyMultiplier *100;
        inputTotalPoints.setText(points + "");
        return points;
    }

}