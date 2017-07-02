package com.example.movies.model.api;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Request {

    private final ServerMethod mMethod;
    private List<Pair<String, String>> mParameters;
    private List<String> mPathParameters;

    public Request(ServerMethod method) {
        mMethod = method;
        mParameters = new ArrayList<>();
        mPathParameters = new ArrayList<>();
    }

    ServerMethod getMethod() {
        return mMethod;
    }

    List<Pair<String, String>> getParameters() {
        return mParameters;
    }

    public void addParameter(String name, Object value) {
        mParameters.add(new Pair<String, String>(name, value.toString()));
    }

    List<String> getPathParameters() {
        return mPathParameters;
    }

    public void addPathParameter(String value) {
        mPathParameters.add(value);
    }

    @Override
    public String toString() {
        return "Request{" +
                "mMethod=" + mMethod +
                '}';
    }
}
