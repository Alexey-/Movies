package com.example.movies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.movies.model.api.Request;
import com.example.movies.model.api.Response;
import com.example.movies.model.api.Server;
import com.example.movies.model.api.ServerError;
import com.example.movies.model.api.ServerMethod;
import com.example.movies.model.base.PageableListLoader;
import com.example.movies.model.storage.MoviesContract;
import com.example.movies.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewsListLoader extends PageableListLoader<Review> {

    private Movie mMovie;

    public ReviewsListLoader(Context context, Movie movie) {
        super(context, MoviesContract.getReviewsUri(movie.getId()));
        mMovie = movie;
    }

    @Override
    protected ServerError performUpdate() {
        Response response = requestPage(1);
        if (response.isSuccessful()) {
            try {
                List<Review> reviews = parseReviews(response);
                setReviews(reviews);
                return null;
            } catch (JSONException e) {
                Log.e(Log.DEFAULT_TAG, "Failed to parse reviews", e);
                return ServerError.UNKNOWN_ERROR;
            }
        } else {
            return response.getError();
        }
    }

    @Override
    protected ServerError performLoadNextPage(int pageNumber) {
        Response response = requestPage(pageNumber);
        if (response.isSuccessful()) {
            try {
                List<Review> newReviews = parseReviews(response);
                List<Review> currentReviews = getReviews();
                currentReviews.addAll(newReviews);
                setReviews(currentReviews);
                return null;
            } catch (JSONException e) {
                Log.e(Log.DEFAULT_TAG, "Failed to parse reviews", e);
                return ServerError.UNKNOWN_ERROR;
            }
        } else {
            return response.getError();
        }
    }

    private Response requestPage(int pageNumber) {
        Request request = new Request(ServerMethod.REVIEWS);
        request.addParameter("page", pageNumber);
        request.addPathParameter(mMovie.getId());
        return Server.sendRequest(request);
    }

    private List<Review> parseReviews(Response response) throws JSONException {
        ArrayList<Review> reviews = new ArrayList<>();
        JSONObject json = new JSONObject(response.getResponseText());
        JSONArray array = json.getJSONArray("results");
        for (int i = 0; i < array.length(); ++i) {
            reviews.add(new Review(mMovie, array.getJSONObject(i)));
        }
        mTotalPages = json.getInt("total_pages");
        return reviews;
    }

    private List<Review> getReviews() {
        Cursor cursor = null;
        try {
            List<Review> loadedEnteties = new ArrayList<>();
            cursor = getContext().getContentResolver().query(MoviesContract.getReviewsUri(mMovie.getId()), null, null, null, null);
            while (cursor.moveToNext()) {
                loadedEnteties.add(createEntity(cursor));
            }
            return loadedEnteties;
        } finally {
            cursor.close();
        }
    }

    private void setReviews(List<Review> reviews) {
        ContentValues[] values = new ContentValues[reviews.size()];
        int i = 0;
        for (Review review : reviews) {
            ContentValues value = new ContentValues();
            review.bindToContentValues(value);
            values[i++] = value;
        }

        getContext().getContentResolver().bulkInsert(MoviesContract.getReviewsUri(mMovie.getId()), values);
    }

    @Override
    protected Review createEntity(Cursor cursor) {
        return new Review(mMovie, cursor);
    }

}