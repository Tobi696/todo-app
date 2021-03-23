package com.example.todo;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TodoRepository {
    private Set<Todo> storage = new HashSet<>();
    public final static TodoListSerializer serializer = new JsonTodoListSerializer();
    public final List<ChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    private void notifyChangeListeners() {
        for (ChangeListener changeListener : changeListeners) changeListener.onChanged();
    }

    public void insert(Todo todo) {
        storage.add(todo);
        notifyChangeListeners();
    }

    public void update(Todo todo) {
        storage.remove(todo);
        storage.add(todo);
        notifyChangeListeners();
    }

    public List<Todo> getAllTodos() {
        List<Todo> result = new ArrayList<>();
        result.addAll(storage);
        return result;
    }

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
    }

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
        notifyChangeListeners();
    }
}
