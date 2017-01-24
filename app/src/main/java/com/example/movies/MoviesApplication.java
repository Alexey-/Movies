package com.example.movies;

import android.app.Application;

import com.example.movies.model.Movie;
import com.example.movies.model.MoviesList;
import com.example.movies.utils.Log;

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
