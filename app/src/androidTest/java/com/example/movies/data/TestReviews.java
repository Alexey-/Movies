package com.example.movies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.movies.generators.FakeMoviesGenerator;
import com.example.movies.generators.FakeReviewsGenerator;
import com.example.movies.generators.FakeVideosGenerator;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
import com.example.movies.model.Review;
import com.example.movies.model.Video;
import com.example.movies.model.storage.MoviesContract;
import com.example.movies.model.storage.MoviesDatabaseHelper;
import com.example.movies.utils.TestContentObserver;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestReviews {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        MoviesDatabaseHelper databaseHelper = new MoviesDatabaseHelper(mContext);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        databaseHelper.deleteTables(database);
        databaseHelper.createTables(database);
    }

    public void printDatabase() {
        MoviesDatabaseHelper databaseHelper = new MoviesDatabaseHelper(mContext);
        databaseHelper.printContent();
    }

    public void assertReviewsEqual(Uri uri, Movie movie, List<Review> reviews) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Assert.assertEquals(reviews.size(), cursor.getCount());

        int i = 0;
        while (cursor.moveToNext()) {
            Review reviewFromCursor = new Review(movie, cursor);
            Review originalReview = reviews.get(i++);
            Assert.assertEquals(originalReview, reviewFromCursor);
        }
        cursor.close();
    }

    @Test
    public void testReviews() {
        Movie movie = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);
        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.getReviewsUri(movie.getId()));

        List<Review> randomReviews = new ArrayList<Review>();
        for (int i = 0; i < 10; ++i) {
            randomReviews.add(FakeReviewsGenerator.generateRandomReview(movie, Integer.toString(i)));
        }
        FakeReviewsGenerator.insertReviews(mContext, movie, randomReviews);

        observer.waitForNotificationOrFail(1);
        printDatabase();
        assertReviewsEqual(MoviesContract.getReviewsUri(movie.getId()), movie, randomReviews);
    }

    @Test
    public void testReviewForDifferentMovies() {
        Movie movie1 = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        Movie movie2 = FakeMoviesGenerator.generateRandomMovie(Integer.toString(1));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);

        List<Review> randomReviews1 = new ArrayList<Review>();
        for (int i = 0; i < 10; ++i) {
            randomReviews1.add(FakeReviewsGenerator.generateRandomReview(movie1, Integer.toString(i)));
        }
        FakeReviewsGenerator.insertReviews(mContext, movie1, randomReviews1);

        List<Review> randomReviews2 = new ArrayList<>();
        for (int i = 10; i < 20; ++i) {
            randomReviews2.add(FakeReviewsGenerator.generateRandomReview(movie2, Integer.toString(i)));
        }
        FakeReviewsGenerator.insertReviews(mContext, movie2, randomReviews2);
        printDatabase();
        assertReviewsEqual(MoviesContract.getReviewsUri(movie1.getId()), movie1, randomReviews1);
        assertReviewsEqual(MoviesContract.getReviewsUri(movie2.getId()), movie2, randomReviews2);
    }

    @Test
    public void testReviewsUpdate() {
        Movie movie = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);
        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.getReviewsUri(movie.getId()));

        List<Review> randomReviews = new ArrayList<Review>();
        for (int i = 0; i < 10; ++i) {
            randomReviews.add(FakeReviewsGenerator.generateRandomReview(movie, Integer.toString(i)));
        }
        FakeReviewsGenerator.insertReviews(mContext, movie, randomReviews);

        List<Review> updatedRandomReviews = new ArrayList<Review>();
        for (int i = 0; i < 10; ++i) {
            if (i % 2 == 0) {
                updatedRandomReviews.add(randomReviews.get(i));
            } else {
                updatedRandomReviews.add(FakeReviewsGenerator.generateRandomReview(movie, Integer.toString(i + 10)));
            }
        }
        FakeReviewsGenerator.insertReviews(mContext, movie, updatedRandomReviews);

        observer.waitForNotificationOrFail(2);
        printDatabase();
        assertReviewsEqual(MoviesContract.getReviewsUri(movie.getId()), movie, updatedRandomReviews);
    }

    @After
    public void tearDown() {
        TestContentObserver.destroyAll();
    }

}
