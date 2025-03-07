package com.sp.mad_project;

public class Message {
    private String sender;
    private String content;
    private String type;
    private String date;

    public Message(String sender, String content, String type, String date) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
