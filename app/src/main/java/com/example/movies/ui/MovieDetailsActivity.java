package com.example.movies.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.databinding.MovieDetailsActivityBinding;
import com.example.movies.model.Movie;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String INTENT_PARAM_MOVIE = "INTENT_PARAM_MOVIE";

    private Movie mMovie;

    private MovieDetailsActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = (Movie) getIntent().getSerializableExtra(INTENT_PARAM_MOVIE);
        if (mMovie == null) {
            throw new NullPointerException();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(mMovie.getOriginalTitle());
        mBinding = DataBindingUtil.setContentView(this, R.layout.movie_details_activity);

        int posterWidthPixels = (int) getResources().getDimension(R.dimen.movie_details_poster_width);
        Glide.with(this)
                .load(mMovie.getPosterUrl(Movie.PosterSize.bestFit(posterWidthPixels)))
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
