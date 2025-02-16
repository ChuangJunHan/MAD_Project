package com.sp.mad_project;

public class Notification {
    private String title;
    private String date;

    public Notification(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
