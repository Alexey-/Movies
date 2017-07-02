package com.example.movies.model.api;

import java.util.List;
import java.util.Locale;

public enum ServerMethod {
    POPULAR_MOVIES(HttpMethod.GET, "movie/popular"),
    TOP_RATED_MOVIES(HttpMethod.GET, "movie/top_rated"),
    VIDEOS(HttpMethod.GET, "movie/%s/videos");

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

    String getPath(List<String> pathParameters) {
        if (pathParameters != null && pathParameters.size() > 0) {
            return String.format(Locale.US, mPath, pathParameters.toArray(new String[pathParameters.size()]));
        } else {
            return mPath;
        }
    }

}
