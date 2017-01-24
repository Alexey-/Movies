package com.example.movies.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.movies.model.Movie;
import com.example.movies.model.MoviesList;
import com.example.movies.utils.Log;

public class MoviesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MoviesList topRated = new MoviesList(MoviesList.SortOrder.TOP_RATED);
        topRated.addOnUpdateListener(new MoviesList.OnUpdateListener() {
            @Override
            public void onUpdateComplete() {
                Log.d("Top rated updated");
                Log.d("=====");
                for (Movie movie : topRated.getMovies()) {
                    Log.d(movie.toString());
                }
                Log.d("=====");
            }

            @Override
            public void onUpdateFailed() {
                Log.w("Failed to update top rated movies");
            }
        });
        topRated.update();
    }

}
