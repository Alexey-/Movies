package com.example.movies.model;

import android.text.TextUtils;

import com.example.movies.model.api.Server;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Movie implements Serializable {

    private String mId;
    private String mOriginalTitle;
    private String mPoster;
    private String mPlotSynopsis;
    private Double mUserRating;
    private LocalDate mReleaseDate;

    Movie(JSONObject json) throws JSONException {
        mId = json.getString("id");
        mOriginalTitle = json.getString("original_title");
        mPoster = json.optString("poster_path");
        mPlotSynopsis = json.optString("overview");
        mUserRating = json.optDouble("vote_average");
        String dateAsString = json.optString("release_date");
        if (!TextUtils.isEmpty(dateAsString)) {
            mReleaseDate = LocalDate.parse(dateAsString, DateTimeFormat.forPattern("yyyy-MM-dd"));
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
        builder.append(mPoster);
        return builder.toString();
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public Double getUserRating() {
        return mUserRating;
    }

    public LocalDate getReleaseDate() {
        return mReleaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mId='" + mId + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                '}';
    }

}
