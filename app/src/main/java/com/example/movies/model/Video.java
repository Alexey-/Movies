package com.example.movies.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.StringRes;

import com.example.movies.R;
import com.example.movies.model.storage.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class Video {

    public static class UnknownVideoTypeException extends Exception {}

    public static class UnknownVideoSiteException extends Exception {}

    public static enum Type {
        TRAILER(R.string.video_type_trailer),
        TEASER(R.string.video_type_teaser),
        CLIP(R.string.video_type_clip),
        FEATURETTE(R.string.video_type_featurette);

        private int mTitleRes;

        private Type(@StringRes int titleRes) {
            mTitleRes = titleRes;
        }

        public @StringRes int getTitleRes() {
            return mTitleRes;
        }
    }

    private String mId;
    private Movie mMovie;
    private String mKey;
    private String mName;
    private Type mType;

    public Video(Movie movie, JSONObject json) throws JSONException, UnknownVideoTypeException, UnknownVideoSiteException {
        mMovie = movie;
        if (!"YouTube".equals(json.getString("site"))) {
            throw new UnknownVideoSiteException();
        }
        mId = json.getString("id");
        mKey = json.getString("key");
        mName = json.optString("name");
        try {
            mType = Type.valueOf(json.getString("type").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownVideoTypeException();
        }
    }

    public Video(Movie movie, Cursor cursor) {
        mMovie = movie;
        mId = cursor.getString(cursor.getColumnIndex(MoviesContract.VideosTable._ID));
        mKey = cursor.getString(cursor.getColumnIndex(MoviesContract.VideosTable.COLUMN_KEY));
        mName = cursor.getString(cursor.getColumnIndex(MoviesContract.VideosTable.COLUMN_NAME));
        mType = Type.valueOf(cursor.getString(cursor.getColumnIndex(MoviesContract.VideosTable.COLUMN_TYPE)));
    }

    public void bindToContentValues(ContentValues values) {
        values.put(MoviesContract.VideosTable._ID, mId);
        values.put(MoviesContract.VideosTable.COLUMN_MOVIE_ID, mMovie.getId());
        values.put(MoviesContract.VideosTable.COLUMN_KEY, mKey);
        if (mName == null) {
            values.putNull(MoviesContract.VideosTable.COLUMN_NAME);
        } else {
            values.put(MoviesContract.VideosTable.COLUMN_NAME, mName);
        }
        values.put(MoviesContract.VideosTable.COLUMN_TYPE, mType.toString());
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Type getType() {
        return mType;
    }

    public Uri getTrailerUri() {
        return Uri.parse("https://www.youtube.com/watch?v=" + mKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        if (!mId.equals(video.mId)) return false;
        if (!mMovie.equals(video.mMovie)) return false;
        if (!mKey.equals(video.mKey)) return false;
        if (mName != null ? !mName.equals(video.mName) : video.mName != null) return false;
        return mType == video.mType;

    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mMovie.hashCode();
        result = 31 * result + mKey.hashCode();
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + mType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Video{" +
                "mId='" + mId + '\'' +
                ", mKey='" + mKey + '\'' +
                ", mName='" + mName + '\'' +
                ", mType=" + mType +
                '}';
    }
}
