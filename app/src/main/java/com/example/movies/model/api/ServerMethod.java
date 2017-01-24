package com.example.movies.model.api;

public enum ServerMethod {
    POPULAR_MOVIES(HttpMethod.GET, "movie/popular"),
    TOP_RATED_MOVIES(HttpMethod.GET, "movie/top_rated");

    enum HttpMethod {
        GET,
        POST;
    }

    private final HttpMethod mHttpMethod;
    private final String mPath;

    ServerMethod(HttpMethod httpMethod, String path) {
        mHttpMethod = httpMethod;
        mPath = path;
    }

    HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    String getPath() {
        return mPath;
    }
}
