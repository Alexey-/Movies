package com.example.movies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
import com.example.movies.model.storage.MoviesContract;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FakeMoviesGenerator {

    public static Movie generateRandomMovie(String id) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("original_title", "Movie_" + id);
            json.put("poster_path", "movie" + id + ".jpg");
            json.put("overview", "Movie_" + id + " overview");
            json.put("vote_average", 3);
            json.put("release_date", "2017-10-21");
            return new Movie(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertMovies(Context context, MoviesListType type, List<Movie> movies) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri targetUri;
        switch (type) {
            case MOST_POPULAR:
                targetUri = MoviesContract.MOST_POPULAR_MOVIES_URI;
                break;
            case TOP_RATED:
                targetUri = MoviesContract.TOP_RATED_MOVIES_URI;
                break;
            default:
                throw new RuntimeException("Cannot insert movies of type " + type);
        }
        ContentValues[] values = new ContentValues[movies.size()];
        int i = 0;
        for (Movie movie : movies) {
            values[i] = new ContentValues();
            movie.bindToContentValues(values[i]);
            i++;
        }
        contentResolver.bulkInsert(targetUri, values);
    }

    public static void addToFavorites(Context context, Movie movie) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MoviesTable._ID, movie.getId());
        contentResolver.insert(MoviesContract.FAVORITES_MOVIES_URI, values);
    }

    public static void removeFromFavorites(Context context, Movie movie) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MoviesContract.FAVORITES_MOVIES_URI.buildUpon().appendPath(movie.getId()).build(), null, null);
    }

}
