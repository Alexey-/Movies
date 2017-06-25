package com.example.movies.ui.movies.list;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;

import com.example.movies.R;
import com.example.movies.model.Movie;
import com.example.movies.model.base.LocalListLoader;
import com.example.movies.model.storage.MoviesContract;
import com.example.movies.utils.Log;

import java.util.List;

public class FavoriteMoviesListFragment extends MoviesListFragment {

    private LocalListLoader<Movie> mMoviesListLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoviesListLoader = (LocalListLoader) getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.refreshLayout.setEnabled(false);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new LocalListLoader<Movie>(getContext(), MoviesContract.FAVORITES_MOVIES_URI) {
            @Override
            protected Movie createEntity(Cursor cursor) {
                return new Movie(cursor);
            }
        };
    }

    @Override
    public void onRefresh() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void refreshInterface() {
        if (mAdapter.getItemCount() > 0) {
            Log.v("Have actual data to display");
            mBinding.errorMessage.setVisibility(View.GONE);
            mBinding.recycler.setVisibility(View.VISIBLE);
        } else {
            mBinding.recycler.setVisibility(View.GONE);
            if (mMoviesListLoader.isLoadingLocalData()) {
                Log.v("No data, but load is in progress");
            } else {
                Log.v("No data");
                mBinding.errorMessage.setText(R.string.favorites_no_favorites_yet);
            }
        }
    }

}
