package com.example.pomoz.model_classes;

public class Task {
    private String name, description, location;
    private int tokens;

    public Task(String name, String description, String location, int tokens) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.tokens = tokens;
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
