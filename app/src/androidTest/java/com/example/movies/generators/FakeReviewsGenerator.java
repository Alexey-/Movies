package com.example.movies.generators;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.movies.model.Movie;
import com.example.movies.model.Review;
import com.example.movies.model.storage.MoviesContract;

import org.json.JSONObject;

import java.util.List;

public class FakeReviewsGenerator {

    public static Review generateRandomReview(Movie movie, String reviewId) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", reviewId);
            json.put("author", "Test User");
            json.put("content", "This movie is awesome");
            json.put("url", "http://www.google.com/");
            return new Review(movie, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertReviews(Context context, Movie movie, List<Review> reviews) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri targetUri = MoviesContract.getReviewsUri(movie.getId());
        ContentValues[] values = new ContentValues[reviews.size()];
        int i = 0;
        for (Review review : reviews) {
            values[i] = new ContentValues();
            review.bindToContentValues(values[i]);
            i++;
        }
        contentResolver.bulkInsert(targetUri, values);
    }

}
