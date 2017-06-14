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

public class MoviesListLoader extends UpdatableListLoader<Movie> {

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
        Request request = new Request(getRequestMethod());
        Response response = Server.sendRequest(request);
        if (response.isSuccessful()) {
            try {
                ArrayList<Movie> movies = new ArrayList<>();
                JSONObject json = new JSONObject(response.getResponseText());
                JSONArray array = json.getJSONArray("results");
                for (int i = 0; i < array.length(); ++i) {
                    movies.add(new Movie(array.getJSONObject(i)));
                }

                ContentValues[] values = new ContentValues[movies.size()];
                int i = 0;
                for (Movie movie : movies) {
                    ContentValues value = new ContentValues();
                    movie.bindToContentValues(value);
                    values[i++] = value;
                }

                getContext().getContentResolver().bulkInsert(mMoviesListType.getUri(), values);

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
    protected Movie createEntity(Cursor cursor) {
        return new Movie(cursor);
    }

}
