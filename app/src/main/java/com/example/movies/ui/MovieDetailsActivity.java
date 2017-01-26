package com.example.movies.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.model.Movie;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String INTENT_PARAM_MOVIE = "INTENT_PARAM_MOVIE";

    private Movie mMovie;

    private ImageView mPoster;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mPlot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = (Movie) getIntent().getSerializableExtra(INTENT_PARAM_MOVIE);
        if (mMovie == null) {
            throw new NullPointerException();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(mMovie.getOriginalTitle());
        setContentView(R.layout.movie_details_activity);

        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mUserRating = (TextView) findViewById(R.id.tv_user_rating);
        mPlot = (TextView) findViewById(R.id.tv_plot);

        int posterWidthPixels = (int) getResources().getDimension(R.dimen.movie_details_poster_width);
        Glide.with(mPoster.getContext())
                .load(mMovie.getPosterUrl(Movie.PosterSize.bestFit(posterWidthPixels)))
                .into(mPoster);
        mTitle.setText(mMovie.getOriginalTitle());
        if (mMovie.getReleaseDate() != null) {
            DateTimeFormatter format = DateTimeFormat.mediumDate();
            mReleaseDate.setText(format.print(mMovie.getReleaseDate()));
        } else {
            mReleaseDate.setText("-");
        }
        if (mMovie.getUserRating() != null) {
            DecimalFormat format = new DecimalFormat("#.0");
            mUserRating.setText(format.format(mMovie.getUserRating()));
        } else {
            mReleaseDate.setText("-");
        }
        if (mMovie.getPlotSynopsis() != null) {
            mPlot.setText(mMovie.getPlotSynopsis());
        } else {
            mPlot.setText("-");
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
