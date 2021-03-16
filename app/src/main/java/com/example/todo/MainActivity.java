package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.ActionBar;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements ChangeListener {
    public static final TodoRepository repository = new TodoRepository();
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private List<Todo> todoList = new ArrayList<>();
    private TodoAdapter adapter;
    private ActionBar actionBar;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public void openTodo(Todo todo) {
        getIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = (sharedPrefs, key) -> {
            setTheme();
            onChanged();
        };
        setTheme();
        preferences.registerOnSharedPreferenceChangeListener(listener);
        repository.addChangeListener(this);

        initializeListView();
        updateYourTodosText();

        loadTodos();
    }

    private void setTheme() {
        boolean darkTheme = preferences.getBoolean("dark_theme", false);
        AppCompatDelegate.setDefaultNightMode(darkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private final static int RQ_PREFERENCES = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_button) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RQ_PREFERENCES);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTodos() {
        try {
            InputStream inputStream = openFileInput("todos.todofile");
            repository.load(inputStream);
        } catch (FileNotFoundException e) {
        }
        onChanged();
    }

    private void saveTodos() {
        try {
            OutputStream outputStream = openFileOutput("todos.todofile", MODE_PRIVATE);
            repository.save(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateYourTodosText() {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Your Todos (" + todoList.size() + ")");
    }

    private void initializeListView() {
        ListView todoListView = findViewById(R.id.todosListView);
        adapter = new TodoAdapter(this, R.layout.todo_item_view, todoList, this);
        todoListView.setAdapter(adapter);
    }

    public void startAddTodoIntent(View view) {
        Intent intent = new Intent(this, SingleTodoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onChanged() {
        todoList.clear();
        todoList.addAll(getTodosAccordingToPreferences());
        adapter.notifyDataSetChanged();
        updateYourTodosText();
        saveTodos();
    }

    private List<Todo> getTodosAccordingToPreferences() {
        List<Todo> todos = repository.getAllTodos();
        boolean showCompletedTasks = preferences.getBoolean("show_completed_tasks", true);
        if (showCompletedTasks) return todos;
        return todos.stream().filter((todo) -> !todo.isCompleted()).collect(Collectors.toList());
    }

    public void startEditTodoIntent(Todo todo) {
        Intent intent = new Intent(this, SingleTodoActivity.class);
        intent.putExtra("todo", TodoRepository.serializer.serialize(todo));
        startActivity(intent);
    }
}