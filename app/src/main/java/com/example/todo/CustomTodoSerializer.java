package com.example.todo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomTodoSerializer implements TodoSerializer {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String serialize(Todo todo) {
        String text = todo.getText();
        String date = todo.getDate() == null ? "null" : todo.getDate().format(MainActivity.dateFormatter);
        String time = todo.getTime() == null ? "null" : todo.getTime().format(MainActivity.timeFormatter);
        return "Todo(" + todo.getId() + "," + text + "," + date + "," + time + "," + todo.isCompleted() + ")";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Todo deserialize(String serialized) {
        if (!serialized.startsWith("Todo(") || !serialized.endsWith(")")) {
            return null;
        }
        serialized = serialized.substring(5, serialized.length()-1);
        String[] parts = serialized.split(",");
        String id = parts[0];
        String text = parts[1].trim();
        LocalDate date = parts[2].equals("null") ? null : LocalDate.parse(parts[1], MainActivity.dateFormatter);
        LocalTime time = parts[3].equals("null") ? null : LocalTime.parse(parts[2], MainActivity.timeFormatter);
        boolean completed = Boolean.parseBoolean(parts[4]);
        return new Todo(id, text, date, time, completed);
    }
}
