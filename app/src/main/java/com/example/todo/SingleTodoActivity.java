package com.example.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SingleTodoActivity extends AppCompatActivity {
    Todo todo;
    EditText todoText;
    LocalDate date;
    LocalTime time;
    Button dateButton;
    Button timeButton;
    String realTodoText;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        initializeTodoText();
        initializeDateButton();
        initializeTimeButton();
        initializeSubmitButton();
        initializeWithIntent();
    }

    private void initializeSubmitButton() {
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo = todo == null ? new Todo() : todo;
                todo.setText(realTodoText);
                todo.setDate(date);
                todo.setTime(time);
                if (todo.getText() == null || todo.getText().isEmpty()) return;
                if (todo.getId() == null) {
                    MainActivity.repository.insert(todo);
                } else {
                    MainActivity.repository.update(todo);
                }
            }
        });
    }

    private void initializeDateButton() {
        dateButton = findViewById(R.id.dateButton);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        setDate(null);
    }

    private void showDatePicker() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        DatePicker datePicker = new DatePicker(this);
        alert.setView(datePicker);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar toConvert = Calendar.getInstance();
                toConvert.set(Calendar.YEAR, datePicker.getYear());
                toConvert.set(Calendar.MONTH, datePicker.getMonth());
                toConvert.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                todoText.setText(realTodoText);
                setDate(TimeUtilPro.calendarToLocalDate(toConvert));
            }
        });
        alert.setNegativeButton("Cancel", null);

        if (date != null) {
            Calendar toInit = TimeUtilPro.localDateToCalendar(date);
            datePicker.init(toInit.get(Calendar.YEAR), toInit.get(Calendar.MONTH), toInit.get(Calendar.DAY_OF_MONTH), null);
        }

        alert.show();
    }

    private void initializeTimeButton() {
        timeButton = findViewById(R.id.timeButton);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        setTime(null);
    }

    private void showTimePicker() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        alert.setView(timePicker);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocalTime newTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                setTime(newTime);
            }
        });
        alert.setNegativeButton("Cancel", null);
        if (time != null) {
            timePicker.setHour(time.getHour());
            timePicker.setMinute(time.getMinute());
        }
        alert.show();
    }

    private void initializeWithIntent() {
        Bundle bundle = getIntent().getExtras();
        TextView title = findViewById(R.id.addTodoTitleTextView);
        if (bundle == null) {
            title.setText("Add new Todo");
            submitButton.setText("Add Todo");
        } else {
            title.setText("Modify Todo");
            submitButton.setText("Update Todo");
            todo = TodoRepository.serializer.deserialize(bundle.getString("todo"));
            todoText.setText(todo.getText());
            realTodoText = todo.getText();
            setDate(todo.getDate());
            setTime(todo.getTime());
        }
    }

    private void initializeTodoText() {
        todoText = findViewById(R.id.todoTextEditText);

        todoText.addTextChangedListener(new TextWatcher() {
            Parser parser = new Parser();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = todoText.getText().toString();
                List<DateGroup> dateGroupList = parser.parse(text);
                DateGroup dateGroup = null;
                for (DateGroup dg : dateGroupList) {
                    if (!dg.isRecurring()) {
                        dateGroup = dg;
                        break;
                    }
                }
                LocalDate newDate = date;
                realTodoText = text;
                if (dateGroup != null) {
                    newDate = TimeUtilPro.dateToLocalDate(dateGroup.getDates().get(0));
                    realTodoText = realTodoText.replaceFirst(dateGroup.getText(), "").trim();
                }
                setDate(newDate);
            }
        });
    }

    private void setDate(LocalDate newDate) {
        date = newDate;
        dateButton.setText(date == null ? "No Date" : date.format(MainActivity.dateFormatter));
    }

    private void setTime(LocalTime newTime) {
        time = newTime;
        timeButton.setText(time == null ? "No Time" : time.format(MainActivity.timeFormatter));
    }
}