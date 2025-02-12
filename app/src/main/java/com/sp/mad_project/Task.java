package com.sp.mad_project;

import java.io.Serializable;

public class Task implements Serializable {
    private int id;
    private String name;
    private String deadline;
    private int progress;
    private String assignedMember;
    private int groupId;

    // Constructor
    public Task(int id, String name, String deadline, int progress, String assignedMember, int groupId) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.progress = progress;
        this.assignedMember = assignedMember;
        this.groupId = groupId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getAssignedMember() {
        return assignedMember;
    }

    public void setAssignedMember(String assignedMember) {
        this.assignedMember = assignedMember;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
