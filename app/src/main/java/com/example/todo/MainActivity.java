package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.ActionBar;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    public static final int RQ_WRITE_EXTERNAL_STORAGE = 12345;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RQ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SD Card Access was denied", Toast.LENGTH_LONG).show();
            } else {
                saveTodos(false);
            }
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
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) return;
        File outFile = Environment.getExternalStorageDirectory();
        String path = outFile.getPath();
        String fullPath = path + File.separator + "todos.json";
        try {
            // InputStream inputStream = openFileInput("todos.json");
            InputStream inputStream = new FileInputStream(fullPath);
            repository.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        onChanged();
    }

    private void saveTodos(boolean requestPermission) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (requestPermission) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RQ_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;
            File outFile = Environment.getExternalStorageDirectory();
            String path = outFile.getPath();
            String fullPath = path + File.separator + "todos.json";
            try {
                // OutputStream outputStream = openFileOutput("todos.json", MODE_PRIVATE);
                OutputStream outputStream = new FileOutputStream(fullPath);
                repository.save(outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
        saveTodos(true);
    }

    private List<Todo> getTodosAccordingToPreferences() {
        List<Todo> todos = repository.getAllTodos();
        boolean showCompletedTasks = preferences.getBoolean("show_completed_tasks", true);
        if (showCompletedTasks) return todos;
        return todos.stream().filter((todo) -> !todo.isCompleted()).collect(Collectors.toList());
    }

    public void startEditTodoIntent(Todo todo) {
        Intent intent = new Intent(this, SingleTodoActivity.class);
        List<Todo> todos = new ArrayList<>();
        todos.add(todo);
        intent.putExtra("todo", TodoRepository.serializer.serialize(todos).get(0));
        startActivity(intent);
    }
}