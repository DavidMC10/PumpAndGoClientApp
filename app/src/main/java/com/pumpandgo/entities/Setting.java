package com.pumpandgo.entities;

public class Setting {

    int icon;
    String title, userData;

    public Setting(int icon, String title, String userData) {
        this.icon = icon;
        this.title = title;
        this.userData = userData;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getUserData() {
        return userData;
    }
}
