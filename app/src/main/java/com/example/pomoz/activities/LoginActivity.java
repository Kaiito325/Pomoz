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
import com.example.pomoz.activities.RegisterActivity;
import com.example.pomoz.data.db_config.ApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // jeśli już zalogowany → od razu MainActivity
        if (ApiClient.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.login_activity);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        setupRegisterLink();
        setupLoginButton();
    }

    private void setupRegisterLink() {
        String fullText = "Nie masz konta? Zarejestruj się";
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf("Zarejestruj się");
        int end = start + "Zarejestruj się".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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
        registerLink.setText(spannable);
        registerLink.setMovementMethod(LinkMovementMethod.getInstance());
        registerLink.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

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

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        new Thread(() -> {
            try {
                JSONObject result = ApiClient.getInstance(LoginActivity.this)
                        .login(email, password);

                if (result != null && result.has("access_token")) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Zalogowano!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Nieprawidłowy login lub hasło", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Błąd sieci", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
