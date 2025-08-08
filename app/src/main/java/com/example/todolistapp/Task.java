package com.example.todolistapp;
public class Task {
    public int id;
    public String name;
    public boolean completed;
    public String startTime; // format: "09:00 AM"
    public String endTime;   // format: "10:00 AM"

    public Task(int id, String name, boolean completed, String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.completed = completed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
