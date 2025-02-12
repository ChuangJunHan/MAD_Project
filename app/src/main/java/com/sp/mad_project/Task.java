package com.sp.mad_project;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String name;
    private String deadline;
    private int progress;
    private String assignedMember;

    // Constructor
    public Task(int id, String name, String deadline, int progress, String assignedMember) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.progress = progress;
        this.assignedMember = assignedMember;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public int getProgress() {
        return progress;
    }

    public String getAssignedMember() {
        return assignedMember;
    }
}
