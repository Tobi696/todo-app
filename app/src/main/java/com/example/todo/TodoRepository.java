package com.example.todo;

import android.widget.ListView;

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
    public final static TodoSerializer serializer = new CustomTodoSerializer();
    public final List<ChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    private void notifyChangeListeners() {
        for (ChangeListener changeListener : changeListeners) changeListener.onChanged();
    }

    public void upsert(Todo todo) {
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
        for (Todo todo : storage) {
            pw.println(serializer.serialize(todo));
        }
        pw.flush();
        pw.close();
    }

    public void load(InputStream is) {
        Scanner sc = new Scanner(is);
        storage.clear();
        while (sc.hasNext()) {
            storage.add(serializer.deserialize(sc.nextLine()));
        }
        sc.close();
        notifyChangeListeners();
    }
}
