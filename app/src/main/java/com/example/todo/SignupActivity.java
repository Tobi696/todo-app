package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SignupActivity extends AppCompatActivity {
    private EditText name;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.txtName);
        username = findViewById(R.id.txtUsername2);
        password = findViewById(R.id.txtPassword2);
    }

    public void onSignup(View v) {
        String name = this.name.getText().toString();
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        SignupAction action = new SignupAction(this);
        action.execute(name, username, password);
    }

    public void onHaveAnAccount(View v) {
        finish();
    }

    private class SignupAction extends AsyncTask<String, Integer, Map<String, Object>> {
        private final SignupActivity signupActivity;

        private SignupAction(SignupActivity signupActivity) {
            this.signupActivity = signupActivity;
        }

        @Override
        protected Map<String, Object> doInBackground(String... strings) {
            String name = strings[0];
            String username = strings[1];
            String password = strings[2];
            Map<String, Object> result = new HashMap<>();
            JSONObject postParams = new JSONObject();
            try {
                postParams.put("username", username);
                postParams.put("password", password);
                postParams.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/register.php").openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                byte[] bytes = postParams.toString().getBytes();
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                httpURLConnection.getOutputStream().write(bytes);
                httpURLConnection.getOutputStream().flush();
                if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED && httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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

                postParams = new JSONObject();
                postParams.put("name", "default");
                postParams.put("additionalData", " ");
                httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/todolists.php?username=" + username + "&password=" + password).openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                bytes = postParams.toString().getBytes();
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                httpURLConnection.getOutputStream().write(bytes);
                httpURLConnection.getOutputStream().flush();
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
                JSONObject json = new JSONObject(sb.toString());
                result.put("response_code", httpURLConnection.getResponseCode());
                result.put("json", json);
                result.put("username", username);
                result.put("password", password);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            JSONObject json = (JSONObject) result.get("json");
            if (((Integer)result.get("response_code")) == HttpURLConnection.HTTP_OK) {
                Intent intent = new Intent(signupActivity, OverviewActivity.class);
                intent.putExtra("username", (String) result.get("username"));
                intent.putExtra("password", (String) result.get("password"));
                try {
                    intent.putExtra("todoListId", json.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                signupActivity.startActivity(intent);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(signupActivity);
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