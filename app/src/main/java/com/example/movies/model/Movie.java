package com.example.movies.model;

import android.text.TextUtils;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

public class Movie {

    private String mId;
    private String mOriginalTitle;
    private String mThumbnailUrl;
    private String mPlotSynopsis;
    private Double mUserRating;
    private LocalDate mReleaseDate;

    Movie(JSONObject json) throws JSONException {
        mId = json.getString("id");
        mOriginalTitle = json.getString("original_title");
        mThumbnailUrl = json.optString("poster_path");
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

    public String getThumbnailUrl() {
        return mThumbnailUrl;
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
