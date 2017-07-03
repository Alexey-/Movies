package com.example.movies.ui.movies.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.databinding.MovieDetailsFragmentBinding;
import com.example.movies.model.Movie;
import com.example.movies.model.Video;
import com.example.movies.model.VideosListLoader;
import com.example.movies.ui.BaseFragment;
import com.example.movies.utils.DIP;

import java.util.List;

public class MovieDetailsFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Video>>, VideosListLoader.OnUpdateListener{

    private static final String ARGUMENT_MOVIE_ID = "ARGUMENT_MOVIE_ID";

    public static MovieDetailsFragment createFragment(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_MOVIE_ID, movie.getId());

        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Movie mMovie;

    private MovieDetailsAdapter mAdapter;
    private MovieDetailsFragmentBinding mBinding;
    private boolean mUseDoubleColumnLayout;

    private VideosListLoader mVideosListLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = Movie.loadFromDatabase(getContext(), getArguments().getString(ARGUMENT_MOVIE_ID));
        if (mMovie == null) {
            throw new NullPointerException();
        }
        mAdapter = new MovieDetailsAdapter(mMovie);
        mVideosListLoader = (VideosListLoader) getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MovieDetailsFragmentBinding.inflate(getLayoutInflater(savedInstanceState), container, false);

        mBinding.recycler.setAdapter(mAdapter);
        mBinding.refreshLayout.setOnRefreshListener(this);

        boolean tablet = getResources().getBoolean(R.bool.is_tablet);
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels;
        int viewWidthPx;
        if (tablet) {
            viewWidthPx = screenWidthPx - getResources().getDimensionPixelSize(R.dimen.tablet_left_pane_width);
        } else {
            viewWidthPx = screenWidthPx;
        }
        int viewWidthDp = DIP.toDp(viewWidthPx);
        mUseDoubleColumnLayout = viewWidthDp > 400;

        RecyclerView.LayoutManager layoutManager;
        if (mUseDoubleColumnLayout) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (mAdapter.getItemViewType(position)) {
                        case MovieDetailsAdapter.ITEM_VIEW_TYPE_MOVIE_DESCRIPTION:
                        case MovieDetailsAdapter.ITEM_VIEW_TYPE_VIDEO_TYPE:
                            return 2;
                        default:
                            return 1;
                    }
                }
            });
            layoutManager = gridLayoutManager;
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        mBinding.recycler.setLayoutManager(layoutManager);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideosListLoader.addOnUpdateListener(this);
        if (mVideosListLoader.needsUpdate()) {
            mVideosListLoader.update();
        }
        refreshInterface();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideosListLoader.removeOnUpdateListener(this);
    }

    @Override
    public String getTitle() {
        return mMovie.getOriginalTitle();
    }

    @Override
    public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
        return new VideosListLoader(getContext(), mMovie);
    }

    @Override
    public void onLoadFinished(Loader<List<Video>> loader, List<Video> data) {
        mAdapter.setVideos(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Video>> loader) {
        mAdapter.setVideos(null);
    }

    @Override
    public void onRefresh() {
        mVideosListLoader.update();
    }

    @Override
    public void onUpdateStarted() {
        refreshInterface();
    }

    @Override
    public void onUpdateComplete() {
        refreshInterface();
    }

    @Override
    public void onUpdateFailed() {
        showMessage(R.string.update_failed);
        refreshInterface();
    }

    private void refreshInterface() {
        mBinding.refreshLayout.setRefreshing(mVideosListLoader.isUpdating());
    }
}
