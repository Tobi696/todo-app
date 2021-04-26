package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SingleTodoActivity extends AppCompatActivity {
    private Todo todo;
    private EditText todoText;
    private LocalDate date;
    private LocalTime time;
    private Button dateButton;
    private Button timeButton;
    private String realTodoText;
    private Button submitButton;
    private LocationManager locationManager;

    private final int REQUEST_COARSE_LOCATION = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initializeTodoText();
        initializeDateButton();
        initializeTimeButton();
        initializeSubmitButton();
        initializeWithIntent();

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_COARSE_LOCATION) {
            onSubmit(false);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void initializeSubmitButton() {
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit(true);
            }
        });
    }

    private void onSubmit(boolean requestPermission) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (requestPermission) ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        } else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setCostAllowed(false);
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = null;
            try {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    todo.setLatitude(location.getLatitude());
                    todo.setLongitude(location.getLongitude());
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            System.out.println(todo == null);
            todo = todo == null ? new Todo() : todo;
            todo.setText(realTodoText);
            todo.setDate(date);
            todo.setTime(time);
            if (todo.getText() == null || todo.getText().isEmpty()) return;
            if (todo.getId() == null) {
                TodoRepository.instance.insert(todo);
            } else {
                TodoRepository.instance.update(todo);
            }
            finish();
        }
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
            List<String> x = new ArrayList<>();
            x.add(bundle.getString("todo"));
            todo = new CustomTodoListSerializer().deserialize(x).get(0);
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
        dateButton.setText(date == null ? "No Date" : date.format(OverviewActivity.dateFormatter));
    }

    private void setTime(LocalTime newTime) {
        time = newTime;
        timeButton.setText(time == null ? "No Time" : time.format(OverviewActivity.timeFormatter));
    }
}