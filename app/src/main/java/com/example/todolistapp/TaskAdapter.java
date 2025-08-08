package com.example.todolistapp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    Context context;
    ArrayList<Task> tasks;
    TaskDBHelper dbHelper;
    Typeface nataSans;

    public TaskAdapter(Context context, ArrayList<Task> tasks, TaskDBHelper dbHelper) {
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = dbHelper;
        this.nataSans = ResourcesCompat.getFont(context, R.font.natasans_regular);
    }

    @Override
    public int getCount() {
        return tasks.isEmpty() ? 1 : tasks.size(); // Always show 1 item when empty (for the empty card)
    }

    @Override
    public Object getItem(int i) {
        return tasks.isEmpty() ? null : tasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return tasks.isEmpty() ? -1 : tasks.get(i).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Handle empty list case
        if (tasks.isEmpty()) {
            if (convertView == null || convertView.findViewById(R.id.emptyCard) == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.task_empty_item, parent, false);
            }
            return convertView;
        }

        // 2. Handle valid tasks
        Task task = tasks.get(position);

        if (convertView == null || convertView.findViewById(R.id.taskName) == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.taskName);
        TextView timeText = convertView.findViewById(R.id.taskTime);
        CheckBox checkBox = convertView.findViewById(R.id.taskCheck);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        if (nameText == null || timeText == null || checkBox == null || deleteButton == null) {
            return convertView; // Defensive check
        }

        nameText.setText(task.name);
        timeText.setText(task.startTime + " - " + task.endTime);
        checkBox.setChecked(task.completed);

        nameText.setTypeface(nataSans, Typeface.BOLD);
        timeText.setTypeface(nataSans);

        nameText.setPaintFlags(checkBox.isChecked()
                ? nameText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                : nameText.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.completed = isChecked;
            dbHelper.updateTask(task);
            notifyDataSetChanged();
        });

        deleteButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle("Delete Task");
            dialog.setMessage("Are you sure you want to delete this task?");
            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(context, R.drawable.dialog_background));

            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (d, which) -> {
                dbHelper.deleteTask(task.id);
                tasks.remove(position);
                notifyDataSetChanged();
            });

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (d, which) -> dialog.dismiss());
            dialog.show();

            TextView message = dialog.findViewById(android.R.id.message);
            if (message != null) {
                message.setTypeface(nataSans);
            }
        });

        convertView.setOnClickListener(v -> showEditDialog(task));

        return convertView;
    }

    private void showEditDialog(Task task) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        EditText nameInput = new EditText(context);
        nameInput.setHint("Task name");
        nameInput.setText(task.name);
        nameInput.setTypeface(nataSans);
        layout.addView(nameInput);

        final String[] startTime = {task.startTime};
        final String[] endTime = {task.endTime};

        TextView startTimeView = new TextView(context);
        startTimeView.setText("Start time: " + startTime[0]);
        startTimeView.setPadding(0, 16, 0, 16);
        startTimeView.setTypeface(nataSans);
        layout.addView(startTimeView);
        startTimeView.setOnClickListener(v -> showTimePickerDialog(startTime, startTimeView));

        TextView endTimeView = new TextView(context);
        endTimeView.setText("End time: " + endTime[0]);
        endTimeView.setPadding(0, 16, 0, 16);
        endTimeView.setTypeface(nataSans);
        layout.addView(endTimeView);
        endTimeView.setOnClickListener(v -> showTimePickerDialog(endTime, endTimeView));

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Edit Task");
        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.dialog_background));

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (d, which) -> {
            String newName = nameInput.getText().toString().trim();
            if (!newName.isEmpty()) {
                task.name = newName;
                task.startTime = startTime[0];
                task.endTime = endTime[0];
                dbHelper.updateTask(task);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (d, which) -> dialog.dismiss());
        dialog.show();
    }

    private void showTimePickerDialog(String[] timeHolder, TextView timeView) {
        TimePickerDialog timePicker = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute);
            timeHolder[0] = time;
            timeView.setText(timeView.getText().toString().split(": ")[0] + ": " + time);
        }, 12, 0, true);
        timePicker.show();
    }
}
