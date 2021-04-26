package com.example.todo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CustomTodoListSerializer implements TodoListSerializer {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<String> serialize(List<Todo> todos) {
        List<String> result = new ArrayList<>();
        for (Todo todo : todos) {
            String text = todo.getText();
            String date = todo.getDate() == null ? "null" : todo.getDate().format(OverviewActivity.dateFormatter);
            String time = todo.getTime() == null ? "null" : todo.getTime().format(OverviewActivity.timeFormatter);
            result.add("Todo(" + todo.getId() + "," + text + "," + date + "," + time + "," + todo.isCompleted() + ")");
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Todo> deserialize(List<String> todosSerialized) {
        List<Todo> result = new ArrayList<>(todosSerialized.size());
        for (String serialized : todosSerialized) {
            if (!serialized.startsWith("Todo(") || !serialized.endsWith(")")) {
                return null;
            }
            serialized = serialized.substring(5, serialized.length() - 1);
            String[] parts = serialized.split(",");
            String id = parts[0];
            String text = parts[1].trim();
            LocalDate date = parts[2].equals("null") ? null : LocalDate.parse(parts[2], OverviewActivity.dateFormatter);
            LocalTime time = parts[3].equals("null") ? null : LocalTime.parse(parts[3], OverviewActivity.timeFormatter);
            double latitude = Double.parseDouble(parts[4]);
            double longitude = Double.parseDouble(parts[5]);
            String address = parts[6];
            boolean completed = Boolean.parseBoolean(parts[7]);
            result.add(new Todo(id, text, date, time, latitude, longitude, address, completed));
        }
        return result;
    }
}
