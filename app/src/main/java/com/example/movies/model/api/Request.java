package com.example.movies.model.api;

public class Request {

    private final ServerMethod mMethod;

    public Request(ServerMethod method) {
        mMethod = method;
    }

    ServerMethod getMethod() {
        return mMethod;
    }

    @Override
    public String toString() {
        return "Request{" +
                "mMethod=" + mMethod +
                '}';
    }
}
