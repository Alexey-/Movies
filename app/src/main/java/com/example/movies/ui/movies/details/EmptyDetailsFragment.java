package com.example.movies.ui.movies.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.model.Movie;
import com.example.movies.ui.BaseFragment;

public class EmptyDetailsFragment extends BaseFragment {

    public static EmptyDetailsFragment createFragment() {
        EmptyDetailsFragment fragment = new EmptyDetailsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_details_fragment, container, false);
    }

    @Override
    public String getTitle() {
        return null;
    }
}
