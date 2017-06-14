package com.example.movies.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.movies.R;
import com.example.movies.databinding.MoviesListActivityBinding;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListLoader;
import com.example.movies.model.MoviesListType;
import com.example.movies.model.api.ServerError;
import com.example.movies.utils.Log;

import java.util.List;

public class MoviesListActivity extends AppCompatActivity
        implements MoviesListLoader.OnUpdateListener, SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String BUNDLE_EXTRA_MOVIES_LIST = "BUNDLE_EXTRA_MOVIES_LIST";

    private static final int PREFERRED_CELL_WIDTH_DIP = 185;
    private static final float POSTER_ASPECT_RATE = 1.5027f;

    private int mCellWidthPixels;
    private int mCellHeightPixels;

    private MoviesListActivityBinding mBinding;

    private MoviesListLoader mMoviesListLoader;
    private MoviesListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMoviesListLoader = (MoviesListLoader) getSupportLoaderManager().initLoader(1, null, this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidthPixels = displayMetrics.widthPixels;
        int screenWidthDp = Math.round(screenWidthPixels / displayMetrics.density);

        int columnsCount = Math.round(screenWidthDp / (float)PREFERRED_CELL_WIDTH_DIP);
        mCellWidthPixels = screenWidthPixels / columnsCount;
        mCellHeightPixels = Math.round(mCellWidthPixels * POSTER_ASPECT_RATE);

        mBinding = DataBindingUtil.setContentView(this, R.layout.movies_list_activity);

        mBinding.refreshLayout.setOnRefreshListener(this);
        mBinding.recycler.setLayoutManager(new GridLayoutManager(this, columnsCount));

        mAdapter = new MoviesListAdapter(mCellWidthPixels, mCellHeightPixels);
        mBinding.recycler.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMoviesListLoader.addOnUpdateListener(this);
        if (mMoviesListLoader.needsUpdate()) {
            mMoviesListLoader.update();
        }
        refreshInterface();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMoviesListLoader.removeOnUpdateListener(this);
    }

    private void refreshInterface() {
        mBinding.refreshLayout.setRefreshing(mMoviesListLoader.isUpdating());

        if (mMoviesListLoader.hasData()) {
            Log.v("Have actual data to display");
            mBinding.errorMessage.setVisibility(View.GONE);
            mBinding.recycler.setVisibility(View.VISIBLE);
        } else {
            mBinding.recycler.setVisibility(View.GONE);
            if (mMoviesListLoader.isUpdating()) {
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

        switch (mMoviesListLoader.getMoviesListType()) {
            case MOST_POPULAR:
                setTitle(R.string.movies_list_most_popular);
                break;
            case TOP_RATED:
                setTitle(R.string.movies_list_top_rated);
                break;
        }
    }

    private Toast mToast;

    private void showToast(int messageRes) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, messageRes, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onUpdateComplete() {
        showToast(R.string.update_complete);
        refreshInterface();
    }

    @Override
    public void onUpdateFailed() {
        showToast(R.string.update_failed);
        refreshInterface();
    }

    @Override
    public void onRefresh() {
        mMoviesListLoader.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_list_menu, menu);
        return true;
    }

    private void changeSortOrder(MoviesListType newOrder) {
//        if (mMoviesList.getMoviesListType() != newOrder) {
//            mMoviesList.removeOnUpdateListener(this);
//            mMoviesList = new MoviesListLoader(newOrder);
//            mMoviesList.addOnUpdateListener(this);
//            mMoviesList.update();
//            refreshInterface();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_most_popular:
                changeSortOrder(MoviesListType.MOST_POPULAR);
                return true;
            case R.id.action_show_top_rated:
                changeSortOrder(MoviesListType.TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MoviesListLoader(this, MoviesListType.TOP_RATED);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mAdapter.setMovies(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.setMovies(null);
    }
}
