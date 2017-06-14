package com.example.movies.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.databinding.MovieViewHolderBinding;
import com.example.movies.model.Movie;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    private Movie mMovie;

    private MovieViewHolderBinding mBinding;

    public MovieViewHolder(View itemView) {
        super(itemView);
        mBinding = MovieViewHolderBinding.bind(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.INTENT_PARAM_MOVIE_ID, mMovie.getId());
                context.startActivity(intent);
            }
        });
    }

    public void setMovie(Movie movie, int widthPixels) {
        mMovie = movie;
        Glide.with(itemView.getContext())
                .load(movie.getPosterUrl(Movie.PosterSize.bestFit(widthPixels)))
                .into(mBinding.poster);
    }



}
