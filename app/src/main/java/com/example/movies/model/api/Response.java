package com.example.movies.model.api;

public class Response {

    private ServerError mError;
    private String mErrorMessage;
    private String mResponseText;

    Response(ServerError error) {
        mError = error;
    }

    Response(ServerError error, String errorMessage) {
        mError = error;
        mErrorMessage = errorMessage;
    }

    Response(String responseText) {
        mResponseText = responseText;
    }

    public boolean isSuccessful() {
        return mError == null;
    }

    public ServerError getError() {
        return mError;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public String getResponseText() {
        return mResponseText;
    }

}
