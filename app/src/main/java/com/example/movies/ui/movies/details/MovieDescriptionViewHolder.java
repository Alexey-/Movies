package com.example.movies.ui.movies.details;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.databinding.MovieDescriptionViewHolderBinding;
import com.example.movies.model.Movie;
import com.example.movies.utils.CenterInsideDrawable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

public class MovieDescriptionViewHolder extends RecyclerView.ViewHolder {

    private Movie mMovie;

    private MovieDescriptionViewHolderBinding mBinding;

    public MovieDescriptionViewHolder(View itemView) {
        super(itemView);
        mBinding = MovieDescriptionViewHolderBinding.bind(itemView);
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
        int posterWidthPixels = (int) itemView.getResources().getDimension(R.dimen.movie_details_poster_width);
        Movie.PosterSize posterSize = Movie.PosterSize.bestFit(posterWidthPixels);
        Drawable movieIcon = itemView.getResources().getDrawable(R.drawable.ic_movie_gray);
        Drawable placeholder = new CenterInsideDrawable(posterSize.getWidthPixels(), posterSize.getHeightPixels(), movieIcon);
        Glide.with(itemView.getContext())
                .load(mMovie.getPosterUrl(posterSize))
                .dontTransform()
                .placeholder(placeholder)
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

}
