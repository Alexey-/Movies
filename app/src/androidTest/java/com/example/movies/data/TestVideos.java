package com.example.movies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.movies.generators.FakeMoviesGenerator;
import com.example.movies.generators.FakeVideosGenerator;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
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
public class TestVideos {

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

    public void assertVideosEqual(Uri uri, Movie movie, List<Video> videos) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Assert.assertEquals(videos.size(), cursor.getCount());

        int i = 0;
        while (cursor.moveToNext()) {
            Video videoFromCursor = new Video(movie, cursor);
            Video originalVideo = videos.get(i++);
            Assert.assertEquals(originalVideo, videoFromCursor);
        }
        cursor.close();
    }

    @Test
    public void testVideos() {
        Movie movie = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);
        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.MOVIES_URI.buildUpon().appendPath(movie.getId()).appendPath(MoviesContract.PATH_VIDEOS).build());

        List<Video> randomVideos = new ArrayList<Video>();
        for (int i = 0; i < 10; ++i) {
            randomVideos.add(FakeVideosGenerator.generateRandomVideo(movie, Integer.toString(i)));
        }
        FakeVideosGenerator.insertVideos(mContext, movie, randomVideos);

        observer.waitForNotificationOrFail(1);
        printDatabase();
        assertVideosEqual(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie.getId()).appendPath(MoviesContract.PATH_VIDEOS).build(), movie, randomVideos);
    }

    @Test
    public void testVideoForDifferentMovies() {
        Movie movie1 = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        Movie movie2 = FakeMoviesGenerator.generateRandomMovie(Integer.toString(1));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);

        List<Video> randomVideos1 = new ArrayList<Video>();
        for (int i = 0; i < 10; ++i) {
            randomVideos1.add(FakeVideosGenerator.generateRandomVideo(movie1, Integer.toString(i)));
        }
        FakeVideosGenerator.insertVideos(mContext, movie1, randomVideos1);

        List<Video> randomVideos2 = new ArrayList<>();
        for (int i = 10; i < 20; ++i) {
            randomVideos2.add(FakeVideosGenerator.generateRandomVideo(movie2, Integer.toString(i)));
        }
        FakeVideosGenerator.insertVideos(mContext, movie2, randomVideos2);
        printDatabase();
        assertVideosEqual(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie1.getId()).appendPath(MoviesContract.PATH_VIDEOS).build(), movie1, randomVideos1);
        assertVideosEqual(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie2.getId()).appendPath(MoviesContract.PATH_VIDEOS).build(), movie2, randomVideos2);
    }

    @Test
    public void testVideosUpdate() {
        Movie movie = FakeMoviesGenerator.generateRandomMovie(Integer.toString(0));
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);
        FakeMoviesGenerator.insertMovies(mContext, MoviesListType.TOP_RATED, movies);
        TestContentObserver observer = TestContentObserver.observeUri(mContext, MoviesContract.MOVIES_URI.buildUpon().appendPath(movie.getId()).appendPath(MoviesContract.PATH_VIDEOS).build());

        List<Video> randomVideos = new ArrayList<Video>();
        for (int i = 0; i < 10; ++i) {
            randomVideos.add(FakeVideosGenerator.generateRandomVideo(movie, Integer.toString(i)));
        }
        FakeVideosGenerator.insertVideos(mContext, movie, randomVideos);

        List<Video> updatedRandomVideos = new ArrayList<Video>();
        for (int i = 0; i < 10; ++i) {
            if (i % 2 == 0) {
                updatedRandomVideos.add(randomVideos.get(i));
            } else {
                updatedRandomVideos.add(FakeVideosGenerator.generateRandomVideo(movie, Integer.toString(i + 10)));
            }
        }
        FakeVideosGenerator.insertVideos(mContext, movie, updatedRandomVideos);

        observer.waitForNotificationOrFail(2);
        printDatabase();
        assertVideosEqual(MoviesContract.MOVIES_URI.buildUpon().appendPath(movie.getId()).appendPath(MoviesContract.PATH_VIDEOS).build(), movie, updatedRandomVideos);
    }

    @After
    public void tearDown() {
        TestContentObserver.destroyAll();
    }

}
