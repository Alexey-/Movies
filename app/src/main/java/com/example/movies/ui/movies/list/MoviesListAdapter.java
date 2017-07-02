package com.example.movies.ui.movies.list;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.example.movies.R;
import com.example.movies.databinding.MovieViewHolderBinding;
import com.example.movies.model.Movie;
import com.example.movies.utils.CenterInsideDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MovieViewHolder> {

    private List<Movie> mMovies;
    private int mCellWidthPixels;
    private int mCellHeightPixels;
    private OnMovieSelectedListener mOnMovieSelectedListener;

    public MoviesListAdapter() {
        mMovies = Collections.emptyList();
    }

    public void setCellSize(int cellWidthPixels, int cellHeightPixels) {
        mCellWidthPixels = cellWidthPixels;
        mCellHeightPixels = cellHeightPixels;
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        if (movies == null) {
            mMovies = Collections.emptyList();
        } else {
            mMovies = new ArrayList<>(movies);
        }
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view_holder, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.setMovie(mMovies.get(position), mCellWidthPixels);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = mCellHeightPixels;
        holder.itemView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setOnMovieSelectedListener(OnMovieSelectedListener onMovieSelectedListener) {
        mOnMovieSelectedListener = onMovieSelectedListener;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private Movie mMovie;

        private MovieViewHolderBinding mBinding;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mBinding = MovieViewHolderBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMovieSelectedListener != null) {
                        mOnMovieSelectedListener.onMovieSelected(mMovie);
                    }
                }
            });
        }

        public void setMovie(Movie movie, int widthPixels) {
            mMovie = movie;
            Movie.PosterSize posterSize = Movie.PosterSize.bestFit(widthPixels);
            Drawable movieIcon = itemView.getContext().getResources().getDrawable(R.drawable.ic_movie_gray);
            Drawable placeholder = new CenterInsideDrawable(posterSize.getWidthPixels(), posterSize.getHeightPixels(), movieIcon);
            Glide.with(itemView.getContext())
                    .load(movie.getPosterUrl(posterSize))
                    .dontTransform()
                    .placeholder(placeholder)
                    .into(mBinding.poster);
        }
    }

}
