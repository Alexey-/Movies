package com.example.movies.model.base;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.OperationCanceledException;
import android.support.v4.content.AsyncTaskLoader;

import com.example.movies.utils.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class LocalListLoader<E> extends AsyncTaskLoader<List<E>> {

    private Uri mUri;
    private ContentObserver mContentObserver;
    private List<E> mData;

    public LocalListLoader(Context context, Uri uri) {
        super(context);
        mUri = uri;
    }

    protected abstract E createEntity(Cursor cursor);

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (mContentObserver == null) {
            mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public boolean deliverSelfNotifications() {
                    return false;
                }

                @Override
                public void onChange(boolean selfChange) {
                    onContentChanged();
                }

                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    onContentChanged();
                }
            };
            getContext().getContentResolver().registerContentObserver(mUri, false, mContentObserver);
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    public List<E> loadInBackground() {
        Cursor cursor = null;
        try {
            List<E> loadedEnteties = new ArrayList<>();
            cursor = getContext().getContentResolver().query(mUri, null, null, null, null);
            while (cursor.moveToNext()) {
                loadedEnteties.add(createEntity(cursor));
            }
            return loadedEnteties;
        } finally {
            cursor.close();
        }
    }

    @Override
    public void deliverResult(List<E> data) {
        if (isReset()) {
            return;
        }

        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        if (mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mContentObserver);
            mContentObserver = null;
        }
    }

    public boolean hasLocalData() {
        return mData != null && mData.size() > 0;
    }

}
