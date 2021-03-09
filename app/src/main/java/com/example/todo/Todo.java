package com.example.todo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Todo {
    private String text;
    private LocalDate date;
    private LocalTime time;

    public Todo(String text, LocalDate date, LocalTime time) {
        this.text = text;
        this.date = date;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
