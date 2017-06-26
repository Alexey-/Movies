package com.example.movies.model.base;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.movies.model.MoviesListLoader;
import com.example.movies.model.api.ServerError;
import com.example.movies.utils.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class UpdatableListLoader<E> extends LocalListLoader<E> {

    private boolean mUpdating;
    private LocalDateTime mLastUpdate;
    private ServerError mLastError;
    protected ReentrantLock mServerAccessLock = new ReentrantLock();

    public UpdatableListLoader(Context context, Uri uri) {
        super(context, uri);
    }

    public void update() {
        if (mUpdating) {
            return;
        }
        mUpdating = true;
        for (MoviesListLoader.OnUpdateListener listener : mUpdateListeners) {
            listener.onUpdateStarted();
        }

        new AsyncTask<Void, Void, ServerError>() {

            @Override
            protected ServerError doInBackground(Void... params) {
                try {
                    mServerAccessLock.lock();
                    return performUpdate();
                } catch (Exception e) {
                    Log.e(Log.DEFAULT_TAG, "Error during update", e);
                    return ServerError.UNKNOWN_ERROR;
                } finally {
                    mServerAccessLock.unlock();
                }
            }

            @Override
            protected void onPostExecute(ServerError error) {
                mUpdating = false;
                if (error == null) {
                    mLastUpdate = LocalDateTime.now();
                    for (MoviesListLoader.OnUpdateListener listener : mUpdateListeners) {
                        listener.onUpdateComplete();
                    }
                } else {
                    for (MoviesListLoader.OnUpdateListener listener : mUpdateListeners) {
                        listener.onUpdateFailed();
                    }
                }
            }

        }.execute();
    }

    protected abstract ServerError performUpdate();

    public boolean isUpdating() {
        return mUpdating;
    }

    public boolean needsUpdate() {
        return mLastUpdate == null || Minutes.minutesBetween(mLastUpdate, LocalDateTime.now()).getMinutes() > 5;
    }

    public ServerError getLastError() {
        return mLastError;
    }

    public interface OnUpdateListener {
        void onUpdateStarted();
        void onUpdateComplete();
        void onUpdateFailed();
    }

    private List<MoviesListLoader.OnUpdateListener> mUpdateListeners = new ArrayList<>();

    public void addOnUpdateListener(MoviesListLoader.OnUpdateListener listener) {
        mUpdateListeners.add(listener);
    }

    public void removeOnUpdateListener(MoviesListLoader.OnUpdateListener listener) {
        mUpdateListeners.remove(listener);
    }

}
