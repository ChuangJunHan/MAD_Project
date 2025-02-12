package com.sp.mad_project;

public class Drawing {
    private int id;
    private String groupName;
    private String path;

    public Drawing(int id, String groupName, String path) {
        this.id = id;
        this.groupName = groupName;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPath() {
        return path;
    }
}
