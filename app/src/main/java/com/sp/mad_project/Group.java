package com.sp.mad_project;

public class Group {
    private int id;
    private String name;
    private int eventCount;
    private int taskCount;

    public Group(int id, String name, int eventCount, int taskCount) {
        this.id = id;
        this.name = name;
        this.eventCount = eventCount;
        this.taskCount = taskCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getEventCount() {
        return eventCount;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
