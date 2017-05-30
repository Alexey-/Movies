package com.example.movies.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.movies.model.MoviesList;
import com.example.movies.model.api.ServerError;
import com.example.movies.utils.Log;

public class MoviesListActivity extends AppCompatActivity implements MoviesList.OnUpdateListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String BUNDLE_EXTRA_MOVIES_LIST = "BUNDLE_EXTRA_MOVIES_LIST";

    private static final int PREFERRED_CELL_WIDTH_DIP = 185;
    private static final float POSTER_ASPECT_RATE = 1.5027f;

    private MoviesList mMoviesList;

    private int mCellWidthPixels;
    private int mCellHeightPixels;

    private MoviesListActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mMoviesList = new MoviesList(MoviesList.SortOrder.MOST_POPULAR);
        } else {
            mMoviesList = (MoviesList) savedInstanceState.getSerializable(BUNDLE_EXTRA_MOVIES_LIST);
        }

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

        reloadRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_EXTRA_MOVIES_LIST, mMoviesList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMoviesList.addOnUpdateListener(this);
        if (mMoviesList.needsUpdate()) {
            mMoviesList.update();
        }
        refreshInterface();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMoviesList.removeOnUpdateListener(this);
    }

    private void reloadRecyclerView() {
        MoviesListAdapter adapter = new MoviesListAdapter(mMoviesList.getMovies(), mCellWidthPixels, mCellHeightPixels);
        mBinding.recycler.setAdapter(adapter);
    }

    private void refreshInterface() {
        mBinding.refreshLayout.setRefreshing(mMoviesList.isUpdating());

        if (mMoviesList.hasData()) {
            Log.v("Have actual data to display");
            mBinding.errorMessage.setVisibility(View.GONE);
            mBinding.recycler.setVisibility(View.VISIBLE);
        } else {
            mBinding.recycler.setVisibility(View.GONE);
            if (mMoviesList.isUpdating()) {
                Log.v("No data, but update is in progress");
            } else if (mMoviesList.getLastError() == ServerError.NO_INTERNET_CONNECTION) {
                Log.v("No data, no internet");
                mBinding.errorMessage.setText(R.string.error_no_internet);
            } else if (mMoviesList.getLastError() == ServerError.INVALID_API_KEY) {
                mBinding.errorMessage.setText(R.string.error_invalid_api_key);
            } else {
                Log.v("No data, error during last reload");
                mBinding.errorMessage.setText(R.string.error_unknown);
            }
        }

        switch (mMoviesList.getSortOrder()) {
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
        reloadRecyclerView();
        refreshInterface();
    }

    @Override
    public void onUpdateFailed() {
        showToast(R.string.update_failed);
        refreshInterface();
    }

    @Override
    public void onRefresh() {
        mMoviesList.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_list_menu, menu);
        return true;
    }

    private void changeSortOrder(MoviesList.SortOrder newOrder) {
        if (mMoviesList.getSortOrder() != newOrder) {
            mMoviesList.removeOnUpdateListener(this);
            mMoviesList = new MoviesList(newOrder);
            mMoviesList.addOnUpdateListener(this);
            mMoviesList.update();
            refreshInterface();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_most_popular:
                changeSortOrder(MoviesList.SortOrder.MOST_POPULAR);
                return true;
            case R.id.action_show_top_rated:
                changeSortOrder(MoviesList.SortOrder.TOP_RATED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
