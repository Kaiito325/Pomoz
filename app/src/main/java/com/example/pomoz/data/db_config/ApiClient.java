package com.example.pomoz.data.db_config;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = properties.getDbUrl() + "/api.php";
    private static ApiClient instance;
    private SharedPreferences prefs;
    private OkHttpClient client;

    private ApiClient(Context context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl url = original.url();

                    String token = prefs.getString("access_token", null);

                    // Interceptor dodaje token tylko gdy go nie ma
                    if (token != null && url.queryParameter("token") == null) {
                        url = url.newBuilder()
                                .addQueryParameter("token", token)
                                .build();
                    }

                    Request request = original.newBuilder()
                            .url(url)
                            .build();

                    Response response = chain.proceed(request);

                    // Obsługa wygasłego tokena (401)
                    if (response.code() == 401) {
                        response.close();
                        if (refreshToken()) {
                            String newToken = prefs.getString("access_token", null);
                            url = url.newBuilder()
                                    .setQueryParameter("token", newToken)
                                    .build();

                            request = request.newBuilder()
                                    .url(url)
                                    .build();

                            return chain.proceed(request);
                        }
                    }

                    return response;
                })
                .build();
    }

    public static ApiClient getInstance(Context context) {
        if (instance == null) instance = new ApiClient(context);
        return instance;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Response post(String action, Map<String, String> data) throws IOException {

        HttpUrl url = HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addQueryParameter("action", action)
                .build();

        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build();

        return client.newCall(request).execute();
    }
    public Response post(String action) throws IOException {
        HttpUrl url = HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addQueryParameter("action", action)
                .build();

        // Tworzymy pusty body (application/x-www-form-urlencoded)
        RequestBody body = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }


    public Response get(String action, Map<String, String> params) throws IOException {

        HttpUrl.Builder url = HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addQueryParameter("action", action);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url.build())
                .get()
                .build();

        return client.newCall(request).execute();
    }


    private boolean refreshToken() {
        String refresh = prefs.getString("refresh_token", null);
        if (refresh == null) return false;

        OkHttpClient noAuthClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("refresh_token", refresh)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "?action=refresh_token")
                .post(body)
                .build();

        try (Response response = noAuthClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject obj = new JSONObject(response.body().string());

                if (obj.has("access_token")) {
                    prefs.edit()
                            .putString("access_token", obj.getString("access_token"))
                            .apply();
                    return true;
                }
            }
        } catch (Exception ignored) {}

        return false;
    }
    public JSONObject login(String email, String password) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("login", email);
        data.put("haslo", password);

        Response response = post("login", data);

        if (response.body() == null) {
            Log.e("API_LOGIN", "Response body is null");
            return null;
        }

        // odczyt body tylko raz
        String bodyStr = response.body().string();
        Log.d("API_LOGIN", "Code: " + response.code() + ", Body: " + bodyStr);

        if (response.isSuccessful()) {
            try {
                JSONObject obj = new JSONObject(bodyStr);

                // jeśli login się powiódł, zapisujemy tokeny
                if (obj.has("access_token") && obj.has("refresh_token")) {
                    prefs.edit()
                            .putString("access_token", obj.getString("access_token"))
                            .putString("refresh_token", obj.getString("refresh_token"))
                            .putString("imie", obj.optString("imie"))
                            .putString("rola", obj.optString("rola"))
                            .apply();
                }

                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("API_LOGIN", "Login failed with code: " + response.code());
        }

        return null;
    }


    public JSONObject register(String email, String password, String imie, String city) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("login", email);
        data.put("haslo", password);
        data.put("imie", imie);
        data.put("miejscowosc", city);

        Response response = post("register", data);

        if (response.isSuccessful() && response.body() != null) {
            try {
                JSONObject obj = new JSONObject(response.body().string());

                // jeśli rejestracja się powiodła i API zwraca tokeny
                if (obj.has("access_token") && obj.has("refresh_token")) {
                    prefs.edit()
                            .putString("access_token", obj.getString("access_token"))
                            .putString("refresh_token", obj.getString("refresh_token"))
                            .putString("imie", obj.optString("imie"))
                            .putString("rola", obj.optString("rola"))
                            .putString("miejscowosc", obj.optString("miejscowosc"))
                            .apply();
                }

                return obj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public boolean isLoggedIn() {
        String token = prefs.getString("access_token", null);
        return token != null && !token.trim().isEmpty();
    }
    public void logout() {
        prefs.edit()
                .remove("access_token")
                .remove("refresh_token")
                .remove("imie")
                .remove("rola")
                .remove("miejscowosc")
                .apply();
    }
}
