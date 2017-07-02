package com.example.movies.model.storage;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.movies.MoviesApplication;
import com.example.movies.model.Movie;
import com.example.movies.model.Video;

public class MoviesContract {

    public static final String AUTHORITY = "com.example.movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES_BASE = "movies";
    public static final Uri MOVIES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_BASE).build();
    public static final Uri getMovieUri(String movieId) {
        return MOVIES_URI.buildUpon().appendPath(movieId).build();
    }

    public static final String PATH_MOVIES_MOST_POPULAR = "most_popular";
    public static final Uri MOST_POPULAR_MOVIES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_BASE).appendPath(PATH_MOVIES_MOST_POPULAR).build();

    public static final String PATH_MOVIES_TOP_RATED = "top_rated";
    public static final Uri TOP_RATED_MOVIES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_BASE).appendPath(PATH_MOVIES_TOP_RATED).build();

    public static final String PATH_MOVIES_FAVORITES = "favorites";
    public static final Uri FAVORITES_MOVIES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES_BASE).appendPath(PATH_MOVIES_FAVORITES).build();
    public static final Uri getFavoriteMovieUri(String movieId) {
        return FAVORITES_MOVIES_URI.buildUpon().appendPath(movieId).build();
    }

    public static final String PATH_VIDEOS = "videos";
    public static final Uri getVideosUri(String movieId) {
        return MOVIES_URI.buildUpon().appendPath(movieId).appendPath(PATH_VIDEOS).build();
    }
    public static final Uri getVideoUrl(String movieId, String videoId) {
        return MOVIES_URI.buildUpon().appendPath(movieId).appendPath(PATH_VIDEOS).appendPath(videoId).build();
    }

    public static final class MoviesTable implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }

    public static final class MovieListTypeCrossTable {

        public static final String TABLE_NAME = "movies_types";

        public static final String COLUMN_LIST_TYPE = "list_type";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_SORT_ORDER = "sort_order";

    }

    public static final class VideosTable implements BaseColumns {

        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SORT_ID = "sort_id";

    }

}
