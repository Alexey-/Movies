package com.example.movies.model.api;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Request {

    private final ServerMethod mMethod;
    private List<Pair<String, String>> mParameters;

    public Request(ServerMethod method) {
        mMethod = method;
        mParameters = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Request{" +
                "mMethod=" + mMethod +
                '}';
    }
}
