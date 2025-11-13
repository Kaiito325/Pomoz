package com.example.pomoz.model_classes;

import com.example.pomoz.R;

public class Action {
    private String name;
    private int ImgResID;

    public Action(String name, int imgResID) {
        this.name = name;
        ImgResID = imgResID;
    }

    public Action(String name) {
        this.name = name;
        ImgResID = R.drawable.action_icon;
    }

    public String getName() {
        return name;
    }

    public int getImgResID() {
        return ImgResID;
    }
}
