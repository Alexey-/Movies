package com.example.movies.ui.movies.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.databinding.ReviewsListFragmentBinding;
import com.example.movies.model.Movie;
import com.example.movies.model.Review;
import com.example.movies.model.ReviewsListLoader;
import com.example.movies.ui.BaseFragment;
import com.example.movies.utils.DIP;
import com.example.movies.utils.EndlessScrollListener;

import java.util.List;

public class ReviewsListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<Review>>,
        ReviewsListLoader.OnUpdateListener, ReviewsListLoader.OnPagingListener {

    private static final String ARGUMENT_MOVIE_ID = "ARGUMENT_MOVIE_ID";

    public static ReviewsListFragment createFragment(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_MOVIE_ID, movie.getId());

        ReviewsListFragment fragment = new ReviewsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Movie mMovie;

    private ReviewsAdapter mAdapter;
    private ReviewsListFragmentBinding mBinding;
    private boolean mUseDoubleColumnLayout;

    private ReviewsListLoader mReviewsListLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = Movie.loadFromDatabase(getContext(), getArguments().getString(ARGUMENT_MOVIE_ID));
        if (mMovie == null) {
            throw new NullPointerException();
        }
        mAdapter = new ReviewsAdapter();
        mReviewsListLoader = (ReviewsListLoader) getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = ReviewsListFragmentBinding.inflate(getLayoutInflater(savedInstanceState), container, false);

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
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        }
        mBinding.recycler.setLayoutManager(layoutManager);

        mBinding.recycler.addOnScrollListener(new EndlessScrollListener(mBinding.recycler.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mReviewsListLoader.canLoadMore()) {
                    mReviewsListLoader.loadNextPage();
                }
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReviewsListLoader.addOnUpdateListener(this);
        mReviewsListLoader.addOnPagingListener(this);
        if (mReviewsListLoader.needsUpdate()) {
            mReviewsListLoader.update();
        }
        refreshInterface();
    }

    @Override
    public void onPause() {
        super.onPause();
        mReviewsListLoader.removeOnUpdateListener(this);
        mReviewsListLoader.removeOnPagingListener(this);
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
        return new ReviewsListLoader(getContext(), mMovie);
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
        mAdapter.setReviews(data);
        refreshInterface();
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
        mAdapter.setReviews(null);
    }

    @Override
    public void onRefresh() {
        mReviewsListLoader.update();
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
    public void onPagingStarted() {

    }

    @Override
    public void onPagingComplete() {

    }

    @Override
    public void onPagingFailed() {

    }

    @Override
    public void onUpdateFailed() {
        showMessage(R.string.update_failed);
        refreshInterface();
    }

    private void refreshInterface() {
        mBinding.refreshLayout.setRefreshing(mReviewsListLoader.isUpdating());

        if (mReviewsListLoader.isLocalDataEmpty()) {
            if (!mReviewsListLoader.isUpdating() && !mReviewsListLoader.isLoadingLocalData()) {
                mBinding.errorMessage.setVisibility(View.VISIBLE);
                mBinding.errorMessage.setText(R.string.reviews_empty);
            }
        } else {
            mBinding.errorMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

}
