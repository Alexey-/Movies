package com.example.movies.model.base;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.movies.model.MoviesListLoader;
import com.example.movies.model.api.ServerError;
import com.example.movies.utils.Log;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public abstract class PageableListLoader<E> extends UpdatableListLoader<E> {

    private boolean mLoadingNextPage;
    private int mCurrentPage = 1;
    private boolean mCanLoadMore;

    public PageableListLoader(Context context, Uri uri) {
        super(context, uri);
        mCanLoadMore = true;
        addOnUpdateListener(new OnUpdateListener() {
            @Override
            public void onUpdateStarted() {

            }

            @Override
            public void onUpdateComplete() {
                mCurrentPage = 1;
                mCanLoadMore = true;
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
        return mCanLoadMore;
    }

    protected void onEndReached() {
        mCanLoadMore = false;
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
