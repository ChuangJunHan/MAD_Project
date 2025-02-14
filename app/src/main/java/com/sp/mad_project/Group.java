package com.sp.mad_project;

public class Group {
    private int id;
    private String name;
    private int eventCount;
    private int taskOrGanttCount;

    public Group(int id, String name, int eventCount, int taskOrGanttCount) {
        this.id = id;
        this.name = name;
        this.eventCount = eventCount;
        this.taskOrGanttCount = taskOrGanttCount;
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
        return taskOrGanttCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public void setTaskCount(int taskOrGanttCount) {
        this.taskOrGanttCount = taskOrGanttCount;
    }
}
