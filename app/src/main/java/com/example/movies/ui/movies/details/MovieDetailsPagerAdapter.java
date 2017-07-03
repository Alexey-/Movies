package com.example.movies.ui.movies.details;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.movies.R;
import com.example.movies.model.Movie;
import com.example.movies.ui.movies.review.ReviewsListFragment;

public class MovieDetailsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private MovieDetailsFragment mMovieDetailsFragment;
    private ReviewsListFragment mReviewsListFragment;

    public MovieDetailsPagerAdapter(Context context, FragmentManager fragmentManager, Movie movie) {
        super(fragmentManager);
        mContext = context;
        mMovieDetailsFragment = MovieDetailsFragment.createFragment(movie);
        mReviewsListFragment = ReviewsListFragment.createFragment(movie);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return mMovieDetailsFragment;
        } else {
            return mReviewsListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.movie_details_general_description);
        } else {
            return mContext.getString(R.string.movie_details_reviews);
        }
    }
}
