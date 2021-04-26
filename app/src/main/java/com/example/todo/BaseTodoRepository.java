package com.example.todo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseTodoRepository {
    public final List<ChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    protected void notifyChangeListeners() {
        for (ChangeListener changeListener : changeListeners) changeListener.onChanged();
    }

    public void insert(Todo todo) {
        notifyChangeListeners();
    }

    public void update(Todo todo) {
        notifyChangeListeners();
    }

    public abstract List<Todo> getAllTodos();

    public void load(InputStream is) {
        notifyChangeListeners();
    }

    public void save(OutputStream os) {}
}
