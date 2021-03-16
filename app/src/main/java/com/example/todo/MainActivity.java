package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements ChangeListener {
    public static final TodoRepository repository = new TodoRepository();
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private List<Todo> todoList = new ArrayList<>();
    private ArrayAdapter<Todo> adapter;
    private TextView todosText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository.addChangeListener(this);

        initializeListView();
        updateYourTodosText();
    }

    private void updateYourTodosText() {
        todosText = (todosText == null) ? findViewById(R.id.todosText) : todosText;
        todosText.setText("Your Todos (" + todoList.size() + ")");
    }

    private void initializeListView() {
        ListView todoListView = findViewById(R.id.todosListView);
        adapter = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todoList);
        todoListView.setAdapter(adapter);
    }

    public void startAddTodoIntent(View view) {
        Intent intent = new Intent(this, SingleTodoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onChanged() {
        todoList.clear();
        todoList.addAll(repository.getAllTodos());
        adapter.notifyDataSetChanged();
        updateYourTodosText();
    }
}