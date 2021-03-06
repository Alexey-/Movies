package com.example.movies.ui.movies.list;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.databinding.MoviesListFragmentBinding;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
import com.example.movies.ui.BaseFragment;
import com.example.movies.utils.DIP;

import java.util.List;

public abstract class MoviesListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String ARGUMENT_MOVIES_LIST_TYPE = "ARGUMENT_MOVIES_LIST_TYPE";

    public static MoviesListFragment createFragment(MoviesListType listType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGUMENT_MOVIES_LIST_TYPE, listType);

        MoviesListFragment fragment;
        if (listType == MoviesListType.FAVORITES) {
            fragment = new FavoriteMoviesListFragment();
        } else {
            fragment = new PageableMoviesListFragment();
        }
        fragment.setArguments(bundle);
        return fragment;
    }


    private static final int PREFERRED_CELL_WIDTH_DIP = 185;
    private static final float POSTER_ASPECT_RATE = 1.5027f;

    private int mCellWidthPixels;
    private int mCellHeightPixels;

    protected MoviesListFragmentBinding mBinding;

    protected MoviesListAdapter mAdapter;

    protected MoviesListType getMoviesListType() {
        return (MoviesListType) getArguments().getSerializable(ARGUMENT_MOVIES_LIST_TYPE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MoviesListAdapter();
        FragmentActivity parentActivity = getActivity();
        if (parentActivity instanceof OnMovieSelectedListener) {
            mAdapter.setOnMovieSelectedListener((OnMovieSelectedListener) getActivity());
        } else {
            throw new RuntimeException("Parent activity must implement " + OnMovieSelectedListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MoviesListFragmentBinding.inflate(getLayoutInflater(savedInstanceState), container, false);

        mBinding.recycler.setAdapter(mAdapter);
        mBinding.refreshLayout.setOnRefreshListener(this);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            adjustColumnCount((int) getResources().getDimension(R.dimen.tablet_left_pane_width));
        } else {
            adjustColumnCount(getResources().getDisplayMetrics().widthPixels);
        }

        return mBinding.getRoot();
    }

    private void adjustColumnCount(int containerWidthPx) {
        int columnsCount = Math.round(DIP.toDp(containerWidthPx) / (float)PREFERRED_CELL_WIDTH_DIP);
        mCellWidthPixels = containerWidthPx / columnsCount;
        mCellHeightPixels = Math.round(mCellWidthPixels * POSTER_ASPECT_RATE);

        mBinding.recycler.setLayoutManager(new GridLayoutManager(getContext(), columnsCount));
        mAdapter.setCellSize(mCellWidthPixels, mCellHeightPixels);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInterface();
    }

    protected abstract void refreshInterface();

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mAdapter.setMovies(data);
        refreshInterface();
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.setMovies(null);
    }

    @Override
    public String getTitle() {
        switch (getMoviesListType()) {
            case MOST_POPULAR:
                return getContext().getString(R.string.movies_most_popular);
            case TOP_RATED:
                return getContext().getString(R.string.movies_top_rated);
            case FAVORITES:
                return getContext().getString(R.string.movies_favorite);
            default:
                throw new RuntimeException("Unknown movies list type: " + getMoviesListType());
        }
    }
}
