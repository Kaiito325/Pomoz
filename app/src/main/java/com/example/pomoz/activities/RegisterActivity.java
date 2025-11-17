package com.example.pomoz.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pomoz.R;
import com.example.pomoz.activities.MainActivity;
import com.example.pomoz.data.db_config.ApiClient;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button registerButton;
    private TextView loginLink;
    private EditText cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // jeśli już zalogowany → od razu MainActivity
        if (ApiClient.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.register_activity);

        nameInput = findViewById(R.id.nameInput);
        cityInput = findViewById(R.id.cityInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.backToLogin);

        setupLoginLink();
        setupRegisterButton();
    }

    private void setupLoginLink() {
        String fullText = "Masz już konto? Zaloguj się";
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf("Zaloguj się");
        int end = start + "Zaloguj się".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, com.example.pomoz.activities.LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#2196F3")); // niebieski
                ds.setUnderlineText(false);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLink.setText(spannable);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
        loginLink.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Podaj imię");
                nameInput.requestFocus();
                return;
            }
            String city = cityInput.getText().toString().trim();

            if (city.isEmpty()) {
                cityInput.setError("Podaj miejscowość");
                cityInput.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                emailInput.setError("Podaj e-mail");
                emailInput.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                passwordInput.setError("Podaj hasło");
                passwordInput.requestFocus();
                return;
            }

            registerUser(name, email, password, city);
        });
    }

    private void registerUser(String name, String email, String password, String city) {
        new Thread(() -> {
            try {
                JSONObject result = ApiClient.getInstance(RegisterActivity.this)
                        .register(email, password, name, city);

                if (result != null && result.has("access_token")) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Zarejestrowano i zalogowano!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Błąd rejestracji", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Błąd sieci", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
