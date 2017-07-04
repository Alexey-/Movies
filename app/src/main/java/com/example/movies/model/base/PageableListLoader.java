package com.example.movies.model.base;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.movies.model.MoviesListLoader;
import com.example.movies.model.api.ServerError;
import com.example.movies.utils.Log;

import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PageableListLoader<E> extends UpdatableListLoader<E> {

    private boolean mLoadingNextPage;
    protected int mCurrentPage = 1;
    protected int mTotalPages;

    public PageableListLoader(Context context, Uri uri) {
        super(context, uri);
        addOnUpdateListener(new OnUpdateListener() {
            @Override
            public void onUpdateStarted() {

            }

            @Override
            public void onUpdateComplete() {
                mCurrentPage = 1;
            }

            @Override
            public void onUpdateFailed() {

            }
        });
    }

    public void loadNextPage() {
        if (mLoadingNextPage) {
            return;
        }
        mLoadingNextPage = true;
        for (OnPagingListener listener : mPagingListeners) {
            listener.onPagingStarted();
        }

        new AsyncTask<Void, Void, ServerError>() {

            @Override
            protected ServerError doInBackground(Void... params) {
                try {
                    mServerAccessLock.lock();
                    return performLoadNextPage(mCurrentPage + 1);
                } catch (Exception e) {
                    Log.e(Log.DEFAULT_TAG, "Error during loading next page", e);
                    return ServerError.UNKNOWN_ERROR;
                } finally {
                    mServerAccessLock.unlock();
                }
            }

            @Override
            protected void onPostExecute(ServerError error) {
                mLoadingNextPage = false;
                if (error == null) {
                    mCurrentPage++;
                    for (OnPagingListener listener : mPagingListeners) {
                        listener.onPagingComplete();
                    }
                } else {
                    for (OnPagingListener listener : mPagingListeners) {
                        listener.onPagingFailed();
                    }
                }
            }

        }.execute();
    }

    protected abstract ServerError performLoadNextPage(int pageNumber);

    public boolean isLoadingNextPage() {
        return mLoadingNextPage;
    }

    public boolean canLoadMore() {
        return mTotalPages > mCurrentPage;
    }

    public interface OnPagingListener {
        void onPagingStarted();
        void onPagingComplete();
        void onPagingFailed();
    }

    private List<OnPagingListener> mPagingListeners = new ArrayList<>();

    public void addOnPagingListener(OnPagingListener listener) {
        mPagingListeners.add(listener);
    }

    public void removeOnPagingListener(OnPagingListener listener) {
        mPagingListeners.remove(listener);
    }

}
