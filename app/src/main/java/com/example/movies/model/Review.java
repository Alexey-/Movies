package com.example.movies.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.movies.model.storage.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private String mId;
    private Movie mMovie;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public Review(Movie movie, JSONObject json) throws JSONException {
        mMovie = movie;
        mId = json.getString("id");
        mAuthor = json.optString("author");
        mContent = json.optString("content");
        mUrl = json.optString("url");
    }

    public Review(Movie movie, Cursor cursor) {
        mMovie = movie;
        mId = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsTable._ID));
        mAuthor = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsTable.COLUMN_AUTHOR));
        mContent = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsTable.COLUMN_CONTENT));
        mUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsTable.COLUMN_URL));
    }

    public void bindToContentValues(ContentValues values) {
        values.put(MoviesContract.ReviewsTable._ID, mId);
        values.put(MoviesContract.ReviewsTable.COLUMN_MOVIE_ID, mMovie.getId());
        if (mAuthor == null) {
            values.putNull(MoviesContract.ReviewsTable.COLUMN_AUTHOR);
        } else {
            values.put(MoviesContract.ReviewsTable.COLUMN_AUTHOR, mAuthor);
        }
        if (mContent == null) {
            values.putNull(MoviesContract.ReviewsTable.COLUMN_CONTENT);
        } else {
            values.put(MoviesContract.ReviewsTable.COLUMN_CONTENT, mContent);
        }
        if (mUrl == null) {
            values.putNull(MoviesContract.ReviewsTable.COLUMN_URL);
        } else {
            values.put(MoviesContract.ReviewsTable.COLUMN_URL, mUrl);
        }
    }

    public String getId() {
        return mId;
    }

    public Movie getMovie() {
        return mMovie;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "Review{" +
                "mId='" + mId + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (!mId.equals(review.mId)) return false;
        if (mAuthor != null ? !mAuthor.equals(review.mAuthor) : review.mAuthor != null)
            return false;
        if (mContent != null ? !mContent.equals(review.mContent) : review.mContent != null)
            return false;
        return mUrl != null ? mUrl.equals(review.mUrl) : review.mUrl == null;

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mAuthor != null ? mAuthor.hashCode() : 0);
        result = 31 * result + (mContent != null ? mContent.hashCode() : 0);
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        return result;
    }
}
