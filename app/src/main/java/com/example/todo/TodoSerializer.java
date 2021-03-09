package com.example.todo;

public interface TodoSerializer {
    public String serialize(Todo todo);
    public Todo deserialize(String serialized);
}
