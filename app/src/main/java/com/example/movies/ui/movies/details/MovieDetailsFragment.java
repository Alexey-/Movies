package com.example.movies.ui.movies.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.databinding.MovieDetailsFragmentBinding;
import com.example.movies.model.Movie;
import com.example.movies.ui.BaseFragment;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

public class MovieDetailsFragment extends BaseFragment {

    private static final String ARGUMENT_MOVIE_ID = "ARGUMENT_MOVIE_ID";

    public static MovieDetailsFragment createFragment(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_MOVIE_ID, movie.getId());

        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Movie mMovie;

    private MovieDetailsFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMovie = Movie.loadFromDatabase(getContext(), getArguments().getString(ARGUMENT_MOVIE_ID));
        if (mMovie == null) {
            throw new NullPointerException();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = MovieDetailsFragmentBinding.inflate(getLayoutInflater(savedInstanceState), container, false);

        int posterWidthPixels = (int) getResources().getDimension(R.dimen.movie_details_poster_width);
        Glide.with(this)
                .load(mMovie.getPosterUrl(Movie.PosterSize.bestFit(posterWidthPixels)))
                .placeholder(R.drawable.ic_movie_gray)
                .into(mBinding.poster);
        mBinding.title.setText(mMovie.getOriginalTitle());
        if (mMovie.getReleaseDate() != null) {
            DateTimeFormatter format = DateTimeFormat.mediumDate();
            mBinding.releaseDate.setText(format.print(mMovie.getReleaseDate()));
        } else {
            mBinding.releaseDate.setText("-");
        }
        if (mMovie.getVoteAverage() != null) {
            DecimalFormat format = new DecimalFormat("#.0");
            mBinding.userRating.setText(format.format(mMovie.getVoteAverage()));
        } else {
            mBinding.userRating.setText("-");
        }
        if (mMovie.getOverview() != null) {
            mBinding.plot.setText(mMovie.getOverview());
        } else {
            mBinding.plot.setText("-");
        }

        return mBinding.getRoot();
    }

    @Override
    public String getTitle() {
        return mMovie.getOriginalTitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_details, menu);
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
