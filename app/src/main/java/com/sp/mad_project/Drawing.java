package com.sp.mad_project;

public class Drawing {
    private int id;
    private int groupId;
    private String path;

    public Drawing(int id, int groupId, String path) {
        this.id = id;
        this.groupId = groupId;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getPath() {
        return path;
    }
}
