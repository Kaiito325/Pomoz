package com.example.pomoz.model_classes;

import java.io.Serializable;

public class Notification implements Serializable {
    public String title;
    public int id, userId;
    public String message;

    public Notification(int id, int userId, String title, String message) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
    }
}

