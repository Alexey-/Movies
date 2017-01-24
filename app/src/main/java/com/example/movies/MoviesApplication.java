package com.example.movies;

import android.app.Application;

public class MoviesApplication extends Application {

    private static MoviesApplication sInstance;

    public static MoviesApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
