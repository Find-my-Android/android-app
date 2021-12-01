package com.example.findmyandroid;

import android.app.Application;

public class MyAppApplication extends Application {

    private String software_id;
    private String token;

    public String getToken() {
        return token;
    }
    public void setToken(String str) {
        token = str;
    }
    public String getSoftware_id() {
        return software_id;
    }
    public void setSoftware_id(String str) {
        software_id = str;
    }
}