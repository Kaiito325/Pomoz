package com.example.pomoz.model_classes;

import android.content.Context;

import java.io.Serializable;

public class Task implements Serializable {
    private int id, userId, actionId, imgId;
    private String name, description, time, term, difficulty,location;
    private int tokens;

    public Task(String name, String description, String location, int tokens) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.tokens = tokens;
    }

    public Task(int id, int userId, int tokens, int actionId, String name, String imgId, String description, String time, String term, String difficulty, String location, Context context) {
        this(name, description, location, tokens);
        String img = imgId.equals("null")? "action_icon": imgId;
        this.imgId = context.getResources().getIdentifier(img, "drawable", context.getPackageName());
        this.id = id;
        this.userId = userId;
        this.actionId = actionId;
        this.time = time;
        this.term = term;
        this.difficulty = difficulty;
    }

    public int getImgId() {
        return imgId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}
