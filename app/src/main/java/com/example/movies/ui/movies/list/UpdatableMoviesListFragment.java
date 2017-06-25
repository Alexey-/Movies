package com.example.movies.ui.movies.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;

import com.example.movies.R;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListLoader;
import com.example.movies.model.api.ServerError;
import com.example.movies.model.base.UpdatableListLoader;
import com.example.movies.utils.Log;

import java.util.List;

public class UpdatableMoviesListFragment extends MoviesListFragment implements MoviesListLoader.OnUpdateListener {

    private UpdatableListLoader<Movie> mMoviesListLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoviesListLoader = (MoviesListLoader) getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMoviesListLoader.addOnUpdateListener(this);
        if (mMoviesListLoader.needsUpdate()) {
            mMoviesListLoader.update();
        }
        refreshInterface();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMoviesListLoader.removeOnUpdateListener(this);
    }

    @Override
    public void onUpdateComplete() {
        showMessage(R.string.update_complete);
        refreshInterface();
    }

    @Override
    public void onUpdateFailed() {
        showMessage(R.string.update_failed);
        refreshInterface();
    }

    @Override
    public void onRefresh() {
        mMoviesListLoader.update();
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MoviesListLoader(getContext(), getMoviesListType());
    }

    @Override
    protected void refreshInterface() {
        mBinding.refreshLayout.setRefreshing(mMoviesListLoader.isUpdating());

        if (mAdapter.getItemCount() > 0) {
            Log.v("Have actual data to display");
            mBinding.errorMessage.setVisibility(View.GONE);
            mBinding.recycler.setVisibility(View.VISIBLE);
        } else {
            mBinding.recycler.setVisibility(View.GONE);
            if (mMoviesListLoader.isLoadingLocalData() || mMoviesListLoader.isUpdating()) {
                Log.v("No data, but update is in progress");
            } else if (mMoviesListLoader.getLastError() == ServerError.NO_INTERNET_CONNECTION) {
                Log.v("No data, no internet");
                mBinding.errorMessage.setText(R.string.error_no_internet);
            } else if (mMoviesListLoader.getLastError() == ServerError.INVALID_API_KEY) {
                mBinding.errorMessage.setText(R.string.error_invalid_api_key);
            } else {
                Log.v("No data, error during last reload");
                mBinding.errorMessage.setText(R.string.error_unknown);
            }
        }
    }
}
