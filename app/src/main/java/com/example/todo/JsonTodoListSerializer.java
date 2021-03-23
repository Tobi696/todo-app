package com.example.todo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonTodoListSerializer implements TodoListSerializer {
    private final Gson gson = new Gson();
    private final Type typeToken = new TypeToken<ArrayList<Todo>>() {}.getType();

    @Override
    public List<String> serialize(List<Todo> todo) {
        List<String> lines = new ArrayList<>();
        lines.add(gson.toJson(todo, typeToken));
        return lines;
    }

    @Override
    public List<Todo> deserialize(List<String> serialized) {
        List<Todo> todos = gson.fromJson(serialized.get(0), typeToken);
        return todos;
    }
}
