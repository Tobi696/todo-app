package com.example.todo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TodoSerializer {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String serialize(Todo todo) {
        String text = todo.getText();
        String date = todo.getDate() == null ? "null" : todo.getDate().format(MainActivity.dateFormatter);
        String time = todo.getTime() == null ? "null" : todo.getTime().format(MainActivity.timeFormatter);
        return "Todo(" + text + "," + date + "," + time + ")";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Todo deserialize(String serialized) {
        if (!serialized.startsWith("Todo(") || !serialized.endsWith(")")) {
            return null;
        }
        serialized = serialized.substring(5, serialized.length()-1);
        String[] parts = serialized.split(",");
        String text = parts[0].trim();
        LocalDate date = parts[1].equals("null") ? null : LocalDate.parse(parts[1], MainActivity.dateFormatter);
        LocalTime time = parts[2].equals("null") ? null : LocalTime.parse(parts[2], MainActivity.timeFormatter);
        return new Todo(text, date, time);
    }
}
