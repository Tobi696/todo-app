package com.example.todo;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TodoRepositoryLocal extends BaseTodoRepository {
    private Set<Todo> storage = new HashSet<>();
    public final static TodoListSerializer serializer = new JsonTodoListSerializer();

    @Override
    public void insert(Todo todo) {
        storage.add(todo);
        super.insert(todo);
    }

    @Override
    public void update(Todo todo) {
        storage.remove(todo);
        storage.add(todo);
        super.update(todo);
    }

    @Override
    public List<Todo> getAllTodos() {
        List<Todo> result = new ArrayList<>();
        result.addAll(storage);
        return result;
    }

    @Override
    public void save(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        List<Todo> todos = new ArrayList<>();
        todos.addAll(storage);
        List<String> lines = serializer.serialize(todos);
        for (String line : lines) {
            pw.println(line);
        }
        pw.flush();
        pw.close();
        super.save(os);
    }

    @Override
    public void load(InputStream is) {
        Scanner sc = new Scanner(is);
        StringBuilder content = new StringBuilder();
        List<String> lines = new ArrayList<>();
        while (sc.hasNext()) {
            lines.add(sc.nextLine());
        }
        sc.close();
        storage.clear();
        storage.addAll(serializer.deserialize(lines));
        super.load(is);
    }
}
