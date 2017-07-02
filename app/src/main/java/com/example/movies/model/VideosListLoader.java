package com.example.movies.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.movies.model.api.Request;
import com.example.movies.model.api.Response;
import com.example.movies.model.api.Server;
import com.example.movies.model.api.ServerError;
import com.example.movies.model.api.ServerMethod;
import com.example.movies.model.base.UpdatableListLoader;
import com.example.movies.model.storage.MoviesContract;
import com.example.movies.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideosListLoader extends UpdatableListLoader<Video> {

    private Movie mMovie;

    public VideosListLoader(Context context, Movie movie) {
        super(context, MoviesContract.getVideosUri(movie.getId()));
        mMovie = movie;
    }

    @Override
    protected ServerError performUpdate() {
        Request request = new Request(ServerMethod.VIDEOS);
        request.addPathParameter(mMovie.getId());
        Response response = Server.sendRequest(request);

        if (response.isSuccessful()) {
            try {
                List<Video> videos = new ArrayList<>();
                JSONObject json = new JSONObject(response.getResponseText());
                JSONArray array = json.getJSONArray("results");
                for (int i = 0; i < array.length(); ++i) {
                    try {
                        videos.add(new Video(mMovie, array.getJSONObject(i)));
                    } catch (Video.UnknownVideoTypeException e) {
                        Log.v("Ignoring video with unknown type");
                    } catch (Video.UnknownVideoSiteException e) {
                        Log.v("Ignoring video from unknown video hosting site");
                    }
                }

                ContentValues[] values = new ContentValues[videos.size()];
                int i = 0;
                for (Video video : videos) {
                    ContentValues value = new ContentValues();
                    video.bindToContentValues(value);
                    values[i++] = value;
                }

                getContext().getContentResolver().bulkInsert(MoviesContract.getVideosUri(mMovie.getId()), values);

                return null;
            } catch (JSONException e) {
                Log.e(Log.DEFAULT_TAG, "Failed to parse videos", e);
                return ServerError.UNKNOWN_ERROR;
            }
        } else {
            return response.getError();
        }
    }

    @Override
    protected Video createEntity(Cursor cursor) {
        return new Video(mMovie, cursor);
    }
}
