package com.example.movies.model;

import android.net.Uri;

import com.example.movies.model.storage.MoviesContract;

public enum MoviesListType {
    MOST_POPULAR(MoviesContract.MOST_POPULAR_MOVIES_URI),
    TOP_RATED(MoviesContract.TOP_RATED_MOVIES_URI),
    FAVORITES(MoviesContract.FAVORITES_MOVIES_URI);

    private Uri mUri;

    private MoviesListType(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }
}
