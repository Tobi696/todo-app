package com.example.todo;

import java.util.List;

public interface TodoListSerializer {
    public List<String> serialize(List<Todo> todo);
    public List<Todo> deserialize(List<String> serialized);
}
