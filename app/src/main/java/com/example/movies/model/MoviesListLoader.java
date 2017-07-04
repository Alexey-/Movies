package com.example.movies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.movies.model.api.Request;
import com.example.movies.model.api.Response;
import com.example.movies.model.api.Server;
import com.example.movies.model.api.ServerError;
import com.example.movies.model.api.ServerMethod;
import com.example.movies.model.base.PageableListLoader;
import com.example.movies.model.base.UpdatableListLoader;
import com.example.movies.utils.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoviesListLoader extends PageableListLoader<Movie> {

    private MoviesListType mMoviesListType;

    public MoviesListLoader(Context context, MoviesListType moviesListType) {
        super(context, moviesListType.getUri());
        mMoviesListType = moviesListType;
    }

    public MoviesListType getMoviesListType() {
        return mMoviesListType;
    }

    private ServerMethod getRequestMethod() {
        switch (mMoviesListType) {
            case MOST_POPULAR:
                return ServerMethod.POPULAR_MOVIES;
            case TOP_RATED:
                return ServerMethod.TOP_RATED_MOVIES;
            default:
                throw new RuntimeException("Unknown list type");
        }
    }

    @Override
    protected ServerError performUpdate() {
        Response response = requestPage(1);
        if (response.isSuccessful()) {
            try {
                List<Movie> movies = parseMovies(response);
                setMovies(movies);
                return null;
            } catch (JSONException e) {
                Log.e(Log.DEFAULT_TAG, "Failed to parse movies", e);
                return ServerError.UNKNOWN_ERROR;
            }
        } else {
            return response.getError();
        }
    }

    @Override
    protected ServerError performLoadNextPage(int pageNumber) {
        Response response = requestPage(pageNumber);
        if (response.isSuccessful()) {
            try {
                List<Movie> newMovies = parseMovies(response);
                List<Movie> currentMovies = getMovies();
                currentMovies.addAll(newMovies);
                setMovies(currentMovies);
                return null;
            } catch (JSONException e) {
                Log.e(Log.DEFAULT_TAG, "Failed to parse movies", e);
                return ServerError.UNKNOWN_ERROR;
            }
        } else {
            return response.getError();
        }
    }

    private Response requestPage(int pageNumber) {
        Request request = new Request(getRequestMethod());
        request.addParameter("page", pageNumber);
        return Server.sendRequest(request);
    }

    private List<Movie> parseMovies(Response response) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<>();
        JSONObject json = new JSONObject(response.getResponseText());
        JSONArray array = json.getJSONArray("results");
        for (int i = 0; i < array.length(); ++i) {
            movies.add(new Movie(array.getJSONObject(i)));
        }
        mTotalPages = json.getInt("total_pages");
        return movies;
    }

    private List<Movie> getMovies() {
        Cursor cursor = null;
        try {
            List<Movie> loadedEnteties = new ArrayList<>();
            cursor = getContext().getContentResolver().query(mMoviesListType.getUri(), null, null, null, null);
            while (cursor.moveToNext()) {
                loadedEnteties.add(createEntity(cursor));
            }
            return loadedEnteties;
        } finally {
            cursor.close();
        }
    }

    private void setMovies(List<Movie> movies) {
        ContentValues[] values = new ContentValues[movies.size()];
        int i = 0;
        for (Movie movie : movies) {
            ContentValues value = new ContentValues();
            movie.bindToContentValues(value);
            values[i++] = value;
        }

        getContext().getContentResolver().bulkInsert(mMoviesListType.getUri(), values);
    }

    @Override
    protected Movie createEntity(Cursor cursor) {
        return new Movie(cursor);
    }

}
