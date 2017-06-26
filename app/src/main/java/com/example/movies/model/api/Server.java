package com.example.movies.model.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.example.movies.MoviesApplication;
import com.example.movies.R;
import com.example.movies.utils.InputStreamUtils;
import com.example.movies.utils.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final String TAG = "TMDB-API";
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    public static final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    public static final int DEFAULT_READ_TIMEOUT = 30000;

    private Server() {}

    private static AtomicInteger sRequestNumber = new AtomicInteger();

    private static int nextRequestNumber() {
        return sRequestNumber.incrementAndGet();
    }

    private static String sApiKey;

    private static synchronized String getApiKey() {
        if (sApiKey == null) {
            sApiKey = MoviesApplication.getInstance().getString(R.string.themoviedb_api_key);
            if (TextUtils.isEmpty(sApiKey)) {
                throw new RuntimeException("Please insert your api key into apikey.xml file");
            }
        }
        return sApiKey;
    }

    private static boolean isConnected() {
        Context context = MoviesApplication.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnected();
    }

    public static Response sendRequest(Request request) {
        int requestNumber = nextRequestNumber();
        Log.v(TAG, "[" + requestNumber + "] Executing request: " + request);

        if (isConnected() == false) {
            Log.v(TAG, "[" + requestNumber + "] Aborting: no internet connection");
            return new Response(ServerError.NO_INTERNET_CONNECTION);
        }

        URL url;
        try {
            Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
            uriBuilder.appendEncodedPath(request.getMethod().getPath());
            uriBuilder.appendQueryParameter("api_key", getApiKey());
            for (Pair<String, String> param : request.getParameters()) {
                uriBuilder.appendQueryParameter(param.first, param.second);
            }
            url = new URL(uriBuilder.build().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot create url", e);
        }

        if (Log.LOG_ENABLED) {
            Log.v(TAG, "[" + requestNumber + "] Url: " + url);
        }

        InputStream input = null;
        try  {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
            connection.setRequestMethod(request.getMethod().getHttpMethod().toString());

            connection.setUseCaches(false);
            connection.setDoInput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String responseString = InputStreamUtils.toString(connection.getInputStream());
                Log.logLongText(TAG, "[" + requestNumber + "] Server response: " + responseString);
                return new Response(responseString);
            } else {
                String errorString = InputStreamUtils.toString(connection.getErrorStream());
                Log.w(TAG, "[" + requestNumber + "] Server responded with error code: " + responseCode + " error message: " + errorString);
                if (responseCode == 401) {
                    Log.e(TAG, "[" + requestNumber + "] Invalid API key");
                    return new Response(ServerError.INVALID_API_KEY, errorString);
                } else {
                    return new Response(ServerError.UNKNOWN_ERROR, errorString);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "[" + requestNumber + "] Failed to complete request", e);
            return new Response(ServerError.UNKNOWN_ERROR);
        } finally {
            InputStreamUtils.close(input);
        }
    }

}
