package com.example.movies.generators;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
import com.example.movies.model.Video;
import com.example.movies.model.storage.MoviesContract;

import org.json.JSONObject;

import java.util.List;

public class FakeVideosGenerator {

    public static Video generateRandomVideo(Movie movie, String videoId) {
        try {
            JSONObject json = new JSONObject();
            json.put("id", videoId);
            json.put("key", "5lGoQhFb4NM");
            json.put("name", "Official Comic-Con Trailer");
            json.put("site", "YouTube");
            json.put("type", "Trailer");
            return new Video(movie, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertVideos(Context context, Movie movie, List<Video> videos) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri targetUri = MoviesContract.getVideosUri(movie.getId());
        ContentValues[] values = new ContentValues[videos.size()];
        int i = 0;
        for (Video video : videos) {
            values[i] = new ContentValues();
            video.bindToContentValues(values[i]);
            i++;
        }
        contentResolver.bulkInsert(targetUri, values);
    }

}
