package com.example.todo;

import com.google.gson.Gson;

public class JsonTodoSerializer implements TodoSerializer {
    private final Gson gson = new Gson();

    @Override
    public String serialize(Todo todo) {
        return gson.toJson(todo);
    }

    @Override
    public Todo deserialize(String serialized) {
        return gson.fromJson(serialized, Todo.class);
    }
}
