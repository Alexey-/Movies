package com.example.movies.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.movies.R;
import com.example.movies.model.Movie;

public class MovieViewHolder extends RecyclerView.ViewHolder {

    private ImageView mPoster;

    public MovieViewHolder(View itemView) {
        super(itemView);
        mPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
    }

    public void setMovie(Movie movie, int widthPixels) {
        Glide.with(mPoster.getContext())
                .load(movie.getPosterUrl(Movie.PosterSize.bestFit(widthPixels)))
                .into(mPoster);
    }



}
