package com.example.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TodoRepositoryCloud extends BaseTodoRepository {
    private OverviewActivity overviewActivity;
    private final List<Todo> todos = new ArrayList<>();

    public void setOverviewActivity(OverviewActivity overviewActivity) {
        this.overviewActivity = overviewActivity;
    }

    @Override
    public void insert(Todo todo) {
        InsertAction action = new InsertAction();
        action.execute(overviewActivity.todoListId, todo);
        super.insert(todo);
    }

    @Override
    public void update(Todo todo) {
        UpdateAction action = new UpdateAction();
        action.execute(overviewActivity.todoListId, todo);
        super.update(todo);
    }

    @Override
    public List<Todo> getAllTodos() {
        return todos;
    }

    @Override
    public void load(InputStream is) {
        LoadAction loadAction = new LoadAction();
        loadAction.execute();
        super.load(is);
    }

    private class LoadAction extends AsyncTask<String, Integer, Map<String, Object>> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Map<String, Object> doInBackground(String... strings) {
            Map<String, Object> result = new HashMap<>();
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/todo.php?username=" + overviewActivity.username + "&password=" + overviewActivity.password).openConnection();
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
                return result;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Map<String, Object> result) {
            if (((Integer) result.get("response_code")) == HttpURLConnection.HTTP_OK) {
                JSONArray json = (JSONArray) result.get("json");
                todos.clear();
                for (int i = 0; i < json.length(); ++i) {
                    try {
                        JSONObject jTodo = (JSONObject) json.get(i);
                        if (!jTodo.getString("todoListId").equals(overviewActivity.todoListId)) {
                            continue;
                        }
                        JSONObject additionalData = new JSONObject(jTodo.getString("additionalData"));
                        Todo todo = new Todo(jTodo.getString("id"), jTodo.getString("title"), additionalData.has("date") ? LocalDate.parse(additionalData.getString("date"), OverviewActivity.dateFormatter) : null, additionalData.has("time") ? LocalTime.parse(additionalData.getString("time"), OverviewActivity.timeFormatter) : null, Boolean.parseBoolean(jTodo.getString("state")));
                        todos.add(todo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                notifyChangeListeners();
            } else {
                JSONObject json = (JSONObject) result.get("json");
                AlertDialog.Builder alert = new AlertDialog.Builder(overviewActivity);
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

    private class InsertAction extends AsyncTask<Object, Integer, Map<String, Object>> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Map<String, Object> doInBackground(Object... input) {
            Map<String, Object> result = new HashMap<>();
            String todoListId = (String) input[0];
            Todo todo = (Todo) input[1];
            byte[] bytes = todoToByteResource(todoListId, todo);
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/todo.php?username=" + overviewActivity.username + "&password=" + overviewActivity.password).openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                httpURLConnection.getOutputStream().write(bytes);
                httpURLConnection.getOutputStream().flush();
                if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK || httpURLConnection.getResponseCode() == httpURLConnection.HTTP_CREATED) {
                    Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        builder.append(scanner.nextLine());
                    }
                    JSONObject response = new JSONObject(builder.toString());
                    result.put("success", response);
                } else {
                    Scanner scanner = new Scanner(httpURLConnection.getErrorStream());
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        builder.append(scanner.nextLine());
                    }
                    JSONObject response = new JSONObject(builder.toString());
                    result.put("error", response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            if (result.containsKey("error")) {
                JSONObject error = (JSONObject) result.get("error");
                AlertDialog.Builder builder = new AlertDialog.Builder(overviewActivity);
                builder.setTitle("Something went wrong.");
                try {
                    builder.setMessage(error.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.show();
            } else {
                load(null);
            }
            super.onPostExecute(result);
        }
    }

    private class UpdateAction extends AsyncTask<Object, Integer, Map<String, Object>> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Map<String, Object> doInBackground(Object... input) {
            Map<String, Object> result = new HashMap<>();
            String todoListId = (String) input[0];
            Todo todo = (Todo) input[1];
            byte[] bytes = todoToByteResource(todoListId, todo);
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://sickinger-solutions.at/notesserver/todo.php?id=" + todo.getId() + "&username=" + overviewActivity.username + "&password=" + overviewActivity.password).openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                httpURLConnection.getOutputStream().write(bytes);
                httpURLConnection.getOutputStream().flush();
                if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                    Scanner scanner = new Scanner(httpURLConnection.getInputStream());
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        builder.append(scanner.nextLine());
                    }
                    JSONObject response = new JSONObject(builder.toString());
                    result.put("success", response);
                } else {
                    Scanner scanner = new Scanner(httpURLConnection.getErrorStream());
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        builder.append(scanner.nextLine());
                    }
                    JSONObject response = new JSONObject(builder.toString());
                    result.put("error", response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            if (result.containsKey("error")) {
                JSONObject error = (JSONObject) result.get("error");
                AlertDialog.Builder builder = new AlertDialog.Builder(overviewActivity);
                builder.setTitle("Something went wrong.");
                try {
                    builder.setMessage(error.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.show();
            } else {
                load(null);
            }
            super.onPostExecute(result);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    byte[] todoToByteResource(String todoListId, Todo todo) {
        JSONObject postParams = new JSONObject();
        try {
            postParams.put("todoListId", todoListId);
            postParams.put("title", todo.getText());
            postParams.put("description", todo.getText());
            postParams.put("dueDate", ((todo.getDate() == null ? LocalDate.now() : todo.getDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) + " " + ((todo.getTime() == null ? LocalTime.now() : todo.getTime()).format(DateTimeFormatter.ofPattern("HH:mm"))));
            postParams.put("state", String.valueOf(todo.isCompleted()));
            JSONObject additionalData = new JSONObject();
            additionalData.put("date", todo.getDate() == null ? null : todo.getDate().format(OverviewActivity.dateFormatter));
            additionalData.put("time", todo.getTime() == null ? null : todo.getTime().format(OverviewActivity.timeFormatter));
            postParams.put("additionalData", additionalData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postParams.toString().getBytes();
    }
}
