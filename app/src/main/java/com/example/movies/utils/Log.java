package com.example.movies.utils;

import com.example.movies.BuildConfig;

public final class Log {

    public static final String DEFAULT_TAG = "Movies";
    public static final boolean LOG_ENABLED = true;//BuildConfig.DEBUG;

    public static void logLongText(String tag, String text) {
            v("=========");
            while (text.length() > 0) {
                if (text.length() > 1000) {
                    String firstSymbols = text.substring(0, 1000);
                    text = text.substring(1000, text.length());
                    v(tag, firstSymbols, null);
                } else {
                    v(tag, text, null);
                    text = "";
                }
            }
            v("=========");
    }

    public static final void i(String tag, String string, Throwable throwable) {
        if (LOG_ENABLED) {
            android.util.Log.i(tag, string, throwable);
        }
    }

    public static final void e(String tag, String string, Throwable throwable) {
        if (LOG_ENABLED) {
            android.util.Log.e(tag, string, throwable);
        }
    }

    public static final void d(String tag, String string, Throwable throwable) {
        if (LOG_ENABLED) {
            android.util.Log.d(tag, string, throwable);
        }
    }

    public static final void v(String tag, String string, Throwable throwable) {
        if (LOG_ENABLED) {
            android.util.Log.v(tag, string, throwable);
        }
    }

    public static final void w(String tag, String string, Throwable throwable) {
        if (LOG_ENABLED) {
            android.util.Log.w(tag, string, throwable);
        }
    }

    public static final void i(String string) {
        i(DEFAULT_TAG, string, null);
    }

    public static final void e(String string) {
        e(DEFAULT_TAG, string, null);
    }

    public static final void d(String string) {
        d(DEFAULT_TAG, string, null);
    }

    public static final void v(String string) {
        v(DEFAULT_TAG, string, null);
    }

    public static final void w(String string) {
        w(DEFAULT_TAG, string, null);
    }

    public static final void i(String tag, String string) {
        i(tag, string, null);
    }

    public static final void e(String tag, String string) {
        e(tag, string, null);
    }

    public static final void d(String tag, String string) {
        d(tag, string, null);
    }

    public static final void v(String tag, String string) {
        v(tag, string, null);
    }

    public static final void w(String tag, String string) {
        w(tag, string, null);
    }

}