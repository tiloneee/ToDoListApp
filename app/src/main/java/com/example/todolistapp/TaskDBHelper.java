package com.example.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2;

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "completed INTEGER, " +
                "startTime TEXT, " +
                "endTime TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    // Add new task
    public void addTask(String name, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("completed", 0);
        values.put("startTime", startTime);
        values.put("endTime", endTime);
        db.insert("tasks", null, values);
        db.close();
    }

    // Update existing task
    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", task.name);
        values.put("completed", task.completed ? 1 : 0);
        values.put("startTime", task.startTime);
        values.put("endTime", task.endTime);
        db.update("tasks", values, "id = ?", new String[]{String.valueOf(task.id)});
        db.close();
    }

    // Delete task by ID
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    // Fetch all tasks
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM tasks", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed")) == 1;
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"));

                tasks.add(new Task(id, name, completed, startTime, endTime));
            }
            cursor.close();
        }
        return tasks;
    }
}
