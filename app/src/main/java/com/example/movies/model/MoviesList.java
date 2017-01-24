package com.example.movies.model;

import android.os.AsyncTask;

import com.example.movies.model.api.Request;
import com.example.movies.model.api.Response;
import com.example.movies.model.api.Server;
import com.example.movies.model.api.ServerError;
import com.example.movies.model.api.ServerMethod;
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

public class MoviesList implements Serializable {

    public enum SortOrder {
        MOST_POPULAR,
        TOP_RATED
    }

    private SortOrder mSortOrder;

    private ArrayList<Movie> mMovies;

    private transient boolean mUpdating;
    private LocalDateTime mLastUpdate;
    private ServerError mLastError;

    public MoviesList(SortOrder sortOrder) {
        mSortOrder = sortOrder;
    }

    public List<Movie> getMovies() {
        if (mMovies == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(mMovies);
        }
    }

    public void update() {
        if (mUpdating) {
            return;
        }
        mUpdating = true;

        new AsyncTask<Void, Void, ArrayList<Movie> >() {

            @Override
            protected ArrayList<Movie> doInBackground(Void... params) {
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
                        return movies;
                    } catch (JSONException e) {
                        Log.e(Log.DEFAULT_TAG, "Failed to parse movies", e);
                        mLastError = ServerError.UNKNOWN_ERROR;
                        return null;
                    }
                } else {
                    mLastError = response.getError();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<Movie> movies) {
                mUpdating = false;
                if (movies == null) {
                    for (OnUpdateListener listener : mUpdateListeners) {
                        listener.onUpdateFailed();
                    }
                } else {
                    mMovies = movies;
                    mLastUpdate = LocalDateTime.now();
                    for (OnUpdateListener listener : mUpdateListeners) {
                        listener.onUpdateComplete();
                    }
                }
            }

        }.execute();
    }

    public boolean isUpdating() {
        return mUpdating;
    }

    public boolean needsUpdate() {
        return mLastUpdate == null || Minutes.minutesBetween(mLastUpdate, LocalDateTime.now()).getMinutes() > 5;
    }

    public ServerError getLastError() {
        return mLastError;
    }

    private ServerMethod getRequestMethod() {
        switch (mSortOrder) {
            case MOST_POPULAR:
                return ServerMethod.POPULAR_MOVIES;
            case TOP_RATED:
                return ServerMethod.TOP_RATED_MOVIES;
            default:
                throw new RuntimeException("Unknown sort order");
        }
    }

    public interface OnUpdateListener {
        void onUpdateComplete();
        void onUpdateFailed();
    }

    private transient List<OnUpdateListener> mUpdateListeners = new ArrayList<>();

    public void addOnUpdateListener(OnUpdateListener listener) {
        mUpdateListeners.add(listener);
    }

    public void removeOnUpdateListener(OnUpdateListener listener) {
        mUpdateListeners.remove(listener);
    }

}
