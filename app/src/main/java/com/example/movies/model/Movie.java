package com.example.movies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.movies.model.api.Server;
import com.example.movies.model.storage.MoviesContract;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Movie {

    private static final DateTimeFormatter RELEASE_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    private String mId;
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private LocalDate mReleaseDate;

    public static Movie loadFromDatabase(Context context, String id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MoviesContract.MOVIES_URI.buildUpon().appendPath(id).build(), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return new Movie(cursor);
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Movie(JSONObject json) throws JSONException {
        mId = json.getString("id");
        mOriginalTitle = json.getString("original_title");
        mPosterPath = json.optString("poster_path");
        mOverview = json.optString("overview");
        mVoteAverage = json.optDouble("vote_average");
        String dateAsString = json.optString("release_date");
        if (!TextUtils.isEmpty(dateAsString)) {
            mReleaseDate = LocalDate.parse(dateAsString, RELEASE_DATE_FORMAT);
        }
    }

    public Movie(Cursor cursor) {
        mId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesTable._ID));
        mOriginalTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesTable.COLUMN_ORIGINAL_TITLE));
        mPosterPath = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesTable.COLUMN_POSTER_PATH));
        mOverview = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesTable.COLUMN_OVERVIEW));
        mVoteAverage = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesTable.COLUMN_VOTE_AVERAGE));
        String dateAsString = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesTable.COLUMN_RELEASE_DATE));
        if (!TextUtils.isEmpty(dateAsString)) {
            mReleaseDate = LocalDate.parse(dateAsString, RELEASE_DATE_FORMAT);
        }
    }

    public void bindToContentValues(ContentValues values) {
        values.put(MoviesContract.MoviesTable._ID, mId);
        values.put(MoviesContract.MoviesTable.COLUMN_ORIGINAL_TITLE, mOriginalTitle);
        values.put(MoviesContract.MoviesTable.COLUMN_POSTER_PATH, mPosterPath);
        values.put(MoviesContract.MoviesTable.COLUMN_OVERVIEW, mOverview);
        values.put(MoviesContract.MoviesTable.COLUMN_VOTE_AVERAGE, mVoteAverage);
        if (mReleaseDate != null) {
            values.put(MoviesContract.MoviesTable.COLUMN_RELEASE_DATE, mReleaseDate.toString(RELEASE_DATE_FORMAT));
        } else {
            values.putNull(MoviesContract.MoviesTable.COLUMN_RELEASE_DATE);
        }
    }

    public String getId() {
        return mId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public enum PosterSize {
        EXTRA_SMALL(92, "w92"),
        SMALL(154, "w154"),
        MEDIUM(185, "w185"),
        MEDIUM_LARGE(342, "w342"),
        LARGE(500, "w500"),
        EXTRA_LARGE(780, "w780"),
        ORIGINAL(1024, "original");

        private int mWidthPixels;
        private String mKey;

        private PosterSize(int widthPixels, String key) {
            mWidthPixels = widthPixels;
            mKey = key;
        }

        public static PosterSize bestFit(int widthPixels) {
            int bestDelta = Integer.MAX_VALUE;
            PosterSize bestSize = null;
            for (PosterSize size : values()) {
                int delta = Math.abs(size.mWidthPixels - widthPixels);
                if (delta < bestDelta) {
                    bestDelta = delta;
                    bestSize = size;
                }
            }
            return bestSize;
        }
    }

    public String getPosterUrl(PosterSize size) {
        StringBuilder builder = new StringBuilder();
        builder.append(Server.BASE_IMAGE_URL);
        builder.append(size.mKey);
        builder.append(mPosterPath);
        return builder.toString();
    }

    public String getOverview() {
        return mOverview;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public LocalDate getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (!mId.equals(movie.mId)) return false;
        if (mOriginalTitle != null ? !mOriginalTitle.equals(movie.mOriginalTitle) : movie.mOriginalTitle != null)
            return false;
        if (mPosterPath != null ? !mPosterPath.equals(movie.mPosterPath) : movie.mPosterPath != null)
            return false;
        if (mOverview != null ? !mOverview.equals(movie.mOverview) : movie.mOverview != null)
            return false;
        if (mVoteAverage != null ? !mVoteAverage.equals(movie.mVoteAverage) : movie.mVoteAverage != null)
            return false;
        return mReleaseDate != null ? mReleaseDate.equals(movie.mReleaseDate) : movie.mReleaseDate == null;

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mOriginalTitle != null ? mOriginalTitle.hashCode() : 0);
        result = 31 * result + (mPosterPath != null ? mPosterPath.hashCode() : 0);
        result = 31 * result + (mOverview != null ? mOverview.hashCode() : 0);
        result = 31 * result + (mVoteAverage != null ? mVoteAverage.hashCode() : 0);
        result = 31 * result + (mReleaseDate != null ? mReleaseDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId='" + mId + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                '}';
    }

}

