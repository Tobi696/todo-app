package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
    }

    public void onLogin(View v) {
        String username = txtUsername.getText().toString();
        String password = txtUsername.getText().toString();
        LoginAction action = new LoginAction(this);
        action.execute(username, password);
    }

    public void onCreateAccount(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private class LoginAction extends AsyncTask<String, Integer, Map<String, Object>> {
        private final LoginActivity loginActivity;

        private LoginAction(LoginActivity loginActivity) {
            this.loginActivity = loginActivity;
        }

        @Override
        protected Map<String, Object> doInBackground(String... strings) {
            String username = (String) strings[0];
            String password = (String) strings[1];
            Map<String, Object> result = new HashMap<>();
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/todolists.php?username=" + username + "&password=" + password).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Scanner sc = new Scanner(httpURLConnection.getErrorStream());
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNext()) {
                        sb.append(sc.nextLine());
                    }
                    JSONObject json = new JSONObject(sb.toString());
                    result.put("response_code", httpURLConnection.getResponseCode());
                    result.put("json", json);
                    return result;
                }

                Scanner sc = new Scanner(httpURLConnection.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) {
                    sb.append(sc.nextLine());
                }
                JSONArray json = new JSONArray(sb.toString());
                result.put("response_code", httpURLConnection.getResponseCode());
                result.put("json", json);
                result.put("username", username);
                result.put("password", password);
                return result;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            if (((Integer) result.get("response_code")) == HttpURLConnection.HTTP_OK || ((Integer) result.get("response_code")) == HttpURLConnection.HTTP_CREATED) {
                JSONArray json = (JSONArray) result.get("json");
                String todoListId = null;
                try {
                    JSONObject todoList = (JSONObject) json.get(0);
                    todoListId = todoList.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(loginActivity, OverviewActivity.class);
                intent.putExtra("username", (String) result.get("username"));
                intent.putExtra("password", (String) result.get("password"));
                intent.putExtra("todoListId", todoListId);
                loginActivity.startActivity(intent);
            } else {
                JSONObject json = (JSONObject) result.get("json");
                AlertDialog.Builder alert = new AlertDialog.Builder(loginActivity);
                alert.setTitle("Something went wrong.");
                try {
                    alert.setMessage(json.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alert.show();
            }
            super.onPostExecute(result);
        }
    }
}