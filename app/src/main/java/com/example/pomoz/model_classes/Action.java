package com.example.pomoz.model_classes;

import android.content.Context;

import com.example.pomoz.R;

public class Action {
    private String name;
    private float multiplier;
    private String type;
    private int ImgResID;

    public Action(String name, String imgResID, Context context) {
        this.name = name;
        // imgResID to np. "icon_example"
        this.ImgResID = context.getResources().getIdentifier(imgResID, "drawable", context.getPackageName());
    }

    public Action(String name, float multiplier, String type, String imgResID, Context context) {
        this(name, imgResID, context);
        this.type = type;
        this.multiplier = multiplier;
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

    public float getMultiplier() {
        return multiplier;
    }

    public String getType() {
        return type;
    }
}
