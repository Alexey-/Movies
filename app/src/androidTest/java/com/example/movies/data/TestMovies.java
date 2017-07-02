package com.example.movies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.movies.generators.FakeMoviesGenerator;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
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
public class TestMovies {

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

    public void assertMoviesEqual(Uri uri, List<Movie> movies) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Assert.assertEquals(movies.size(), cursor.getCount());

        int i = 0;
        while (cursor.moveToNext()) {
            Movie movieFromCursor = new Movie(cursor);
            Movie originalMovie = movies.get(i++);
            Assert.assertEquals(originalMovie, movieFromCursor);
        }
        cursor.close();
    }

    @Test
    public void testTopRatedMovies() {
        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.TOP_RATED_MOVIES_URI);

        List<Movie> randomMovies = new ArrayList<Movie>();
        for (int i = 0; i < 10; ++i) {
            randomMovies.add(FakeMoviesGenerator.generateRandomMovie(Integer.toString(i)));
        }
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, randomMovies);

        observer.waitForNotificationOrFail(1);
        printDatabase();
        assertMoviesEqual(MoviesContract.TOP_RATED_MOVIES_URI, randomMovies);
    }

    @Test
    public void testSingleMovie() {
        Movie movie1 = FakeMoviesGenerator.generateRandomMovie("1");
        Movie movie2 = FakeMoviesGenerator.generateRandomMovie("2");
        Movie movie3 = FakeMoviesGenerator.generateRandomMovie("3");

        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);

        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie2.getId()).build(), null, null, null, null);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals(movie2, new Movie(cursor));
        cursor.close();
    }

    @Test
    public void testFavorites() {
        Movie movie1 = FakeMoviesGenerator.generateRandomMovie("1");
        Movie movie2 = FakeMoviesGenerator.generateRandomMovie("2");
        Movie movie3 = FakeMoviesGenerator.generateRandomMovie("3");

        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);

        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);

        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.FAVORITES_MOVIES_URI);
        FakeMoviesGenerator.addToFavorites(mContext, movie3);
        observer.waitForNotificationOrFail(1);
        FakeMoviesGenerator.addToFavorites(mContext, movie2);
        observer.waitForNotificationOrFail(2);

        printDatabase();

        List<Movie> currentFavorites = new ArrayList<>();
        currentFavorites.add(movie3);
        currentFavorites.add(movie2);

        assertMoviesEqual(MoviesContract.MOVIES_URI, movies);
        assertMoviesEqual(MoviesContract.FAVORITES_MOVIES_URI, currentFavorites);

        FakeMoviesGenerator.removeFromFavorites(mContext, movie3);
        observer.waitForNotificationOrFail(3);

        printDatabase();

        currentFavorites = new ArrayList<>();
        currentFavorites.add(movie2);

        assertMoviesEqual(MoviesContract.TOP_RATED_MOVIES_URI, movies);
        assertMoviesEqual(MoviesContract.FAVORITES_MOVIES_URI, currentFavorites);
    }

    @Test
    public void testTopRatedMoviesUpdate() {
        List<Movie> originalMovies = new ArrayList<Movie>();
        for (int i = 0; i < 10; ++i) {
            originalMovies.add(FakeMoviesGenerator.generateRandomMovie(Integer.toString(i)));
        }
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, originalMovies);

        printDatabase();

        List<Movie> modifiedMovies = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            modifiedMovies.add(originalMovies.get(i));
        }
        for (int i = 5; i < 10; ++i) {
            modifiedMovies.add(5, originalMovies.get(i));
        }
        modifiedMovies.remove(3);
        Movie newMovie = FakeMoviesGenerator.generateRandomMovie("20");
        modifiedMovies.add(3, newMovie);
        for (int i = 20; i < 30; ++i) {
            modifiedMovies.add(FakeMoviesGenerator.generateRandomMovie(Integer.toString(i)));
        }
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, modifiedMovies);

        printDatabase();

        assertMoviesEqual(MoviesContract.TOP_RATED_MOVIES_URI, modifiedMovies);
    }

    @Test
    public void testOrphanRemoval() {
        Movie movie1 = FakeMoviesGenerator.generateRandomMovie("1");
        Movie movie2 = FakeMoviesGenerator.generateRandomMovie("2");
        Movie movie3 = FakeMoviesGenerator.generateRandomMovie("3");

        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);

        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.MOST_POPULAR, movies);
        FakeMoviesGenerator.addToFavorites(mContext, movie1);

        List<Movie> shortMovies = new ArrayList<>();
        shortMovies.add(movie2);
        shortMovies.add(movie3);

        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.MOST_POPULAR, shortMovies);
        printDatabase();
        assertMoviesEqual(MoviesContract.MOVIES_URI, movies);

        FakeMoviesGenerator.removeFromFavorites(mContext, movie1);
        printDatabase();
        assertMoviesEqual(MoviesContract.MOVIES_URI, shortMovies);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie1.getId()).build(), null, null, null, null);
        Assert.assertEquals(0, cursor.getCount());
        cursor.close();
    }

    @After
    public void tearDown() {
        TestContentObserver.destroyAll();
    }

}
