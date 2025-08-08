package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ListView taskListView;
    ImageButton fab;
    ArrayList<Task> taskList;
    TaskAdapter adapter;
    TaskDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure this matches your file

        taskListView = findViewById(R.id.taskListView);
        fab = findViewById(R.id.fab);

        dbHelper = new TaskDBHelper(this);
        taskList = dbHelper.getAllTasks();
        adapter = new TaskAdapter(this, taskList, dbHelper);
        taskListView.setAdapter(adapter);

        fab.setOnClickListener(v -> showAddTaskDialog());
        refreshList();
    }

    private void showAddTaskDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 16);

        EditText input = new EditText(this);
        input.setHint("Task name");
        input.setTextSize(16);
        input.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        layout.addView(input);

        TextView startTimeView = new TextView(this);
        startTimeView.setText("Start Time: Not set");
        startTimeView.setPadding(24, 20, 24, 20);
        startTimeView.setTextSize(16);
        startTimeView.setTypeface(Typeface.SANS_SERIF);
        startTimeView.setBackgroundResource(android.R.drawable.editbox_background_normal);
        layout.addView(startTimeView);

        TextView endTimeView = new TextView(this);
        endTimeView.setText("End Time: Not set");
        endTimeView.setPadding(24, 20, 24, 20);
        endTimeView.setTextSize(16);
        endTimeView.setTypeface(Typeface.SANS_SERIF);
        endTimeView.setBackgroundResource(android.R.drawable.editbox_background_normal);
        layout.addView(endTimeView);

        final String[] startTime = {""};
        final String[] endTime = {""};

        startTimeView.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                startTime[0] = String.format("%02d:%02d", hour, minute);
                startTimeView.setText("Start Time: " + startTime[0]);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        endTimeView.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                endTime[0] = String.format("%02d:%02d", hour, minute);
                endTimeView.setText("End Time: " + endTime[0]);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Add New Task");
        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_background));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add", (d, which) -> {
            String taskName = input.getText().toString().trim();
            if (!taskName.isEmpty()) {
                dbHelper.addTask(taskName, startTime[0], endTime[0]);
                refreshList();
            } else {
                Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (d, which) -> dialog.dismiss());
        dialog.show();
    }


    private interface TimeCallback {
        void onTimeSelected(String time);
    }

    private void showTimePicker(TimeCallback callback) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String time = DateFormat.format("HH:mm", hourOfDay * 60 * 60 * 1000L + minute1 * 60 * 1000L).toString();
                    callback.onTimeSelected(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void refreshList() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        adapter.notifyDataSetChanged();
    }
}
