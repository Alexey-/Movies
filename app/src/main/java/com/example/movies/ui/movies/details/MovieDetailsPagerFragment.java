package com.example.movies.ui.movies.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.databinding.MovieDetailsPagerFragmentBinding;
import com.example.movies.model.Movie;
import com.example.movies.ui.BaseFragment;

public class MovieDetailsPagerFragment extends BaseFragment {

    private static final String ARGUMENT_MOVIE_ID = "ARGUMENT_MOVIE_ID";

    public static MovieDetailsPagerFragment createFragment(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_MOVIE_ID, movie.getId());

        MovieDetailsPagerFragment fragment = new MovieDetailsPagerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Movie mMovie;

    private MovieDetailsPagerAdapter mAdapter;
    private MovieDetailsPagerFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMovie = Movie.loadFromDatabase(getContext(), getArguments().getString(ARGUMENT_MOVIE_ID));
        if (mMovie == null) {
            throw new NullPointerException();
        }

        mAdapter = new MovieDetailsPagerAdapter(getContext(), getChildFragmentManager(), mMovie);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MovieDetailsPagerFragmentBinding.inflate(getLayoutInflater(savedInstanceState), container, false);

        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);

        return mBinding.getRoot();
    }

    @Override
    public String getTitle() {
        return mMovie.getOriginalTitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_details, menu);
        MenuItem item = menu.findItem(R.id.favorite);
        if (mMovie.isFavorite(getContext())) {
            item.setIcon(R.drawable.ic_favorite_white);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favorite) {
            if (mMovie.isFavorite(getContext())) {
                mMovie.removeFromFavorites(getContext());
                item.setIcon(R.drawable.ic_favorite_border_white);
            } else {
                mMovie.addToFavorites(getContext());
                item.setIcon(R.drawable.ic_favorite_white);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
