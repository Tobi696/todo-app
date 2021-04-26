package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class OverviewActivity extends AppCompatActivity implements ChangeListener {
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static final int RQ_WRITE_EXTERNAL_STORAGE = 12345;
    public static final int RQ_READ_EXTERNAL_STORAGE = 01234;

    private List<Todo> todoList = new ArrayList<>();
    private TodoAdapter adapter;
    private ActionBar actionBar;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public String username;
    public String password;
    public String todoListId;

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
        TodoRepository.instance.addChangeListener(this);

        initializeListView();
        updateYourTodosText();
        initializeWithIntent();
        loadTodos();
    }

    private void initializeWithIntent() {
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        todoListId = bundle.getString("todoListId");
        if (TodoRepository.instance instanceof TodoRepositoryCloud) {
            ((TodoRepositoryCloud) TodoRepository.instance).setOverviewActivity(this);
        }
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
        TodoRepository.instance.load(null);
    }

    private void saveTodos() {
        TodoRepository.instance.save(null);
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
    }

    private List<Todo> getTodosAccordingToPreferences() {
        List<Todo> todos = TodoRepository.instance.getAllTodos();
        boolean showCompletedTasks = preferences.getBoolean("show_completed_tasks", true);
        if (showCompletedTasks) return todos;
        return todos.stream().filter((todo) -> !todo.isCompleted()).collect(Collectors.toList());
    }

    public void startEditTodoIntent(Todo todo) {
        Intent intent = new Intent(this, SingleTodoActivity.class);
        List<Todo> todos = new ArrayList<>();
        todos.add(todo);
        intent.putExtra("todo", new CustomTodoListSerializer().serialize(todos).get(0));
        startActivity(intent);
    }
}