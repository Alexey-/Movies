package com.example.movies;

import android.app.Application;
import android.os.StrictMode;

public class MoviesApplication extends Application {

    private static MoviesApplication sInstance;

    public static MoviesApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

}
