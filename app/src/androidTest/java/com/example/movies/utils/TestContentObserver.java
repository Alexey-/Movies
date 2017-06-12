package com.example.movies.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.List;

public class TestContentObserver extends ContentObserver {

    private static List<TestContentObserver> activeObservers = new ArrayList<>();

    public static TestContentObserver observeUri(Context context, Uri uri) {
        HandlerThread handlerThread = new HandlerThread("ContentObserverThread");
        handlerThread.start();
        return new TestContentObserver(context, uri, handlerThread);
    }

    public static void destroyAll() {
        List<TestContentObserver> copy = new ArrayList<>(activeObservers);
        for (TestContentObserver observer : copy) {
            observer.destroy();
        }
    }

    private HandlerThread mHandlerThread;
    private Context mContext;
    private Uri mUri;
    private int mTriggered;

    private TestContentObserver(Context context, Uri uri, HandlerThread handlerThread) {
        super(new Handler(handlerThread.getLooper()));
        mHandlerThread = handlerThread;
        mContext = context;
        mUri = uri;

        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.registerContentObserver(mUri, true, this);

        activeObservers.add(this);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (uri.equals(mUri)) {
            mTriggered++;
        }
    }

    public void waitForNotificationOrFail(final int count) {
        new PollingCheck(5000) {
            @Override
            protected boolean check() {
                return mTriggered >= count;
            }
        }.run();
    }

    public void destroy() {
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.unregisterContentObserver(this);
        mHandlerThread.quit();

        activeObservers.remove(this);
    }


}
