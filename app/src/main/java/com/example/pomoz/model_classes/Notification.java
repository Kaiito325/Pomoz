package com.example.pomoz.model_classes;

public class Notification {
    public String title;
    public int id;
    public String message;

    public Notification(int id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }
}

