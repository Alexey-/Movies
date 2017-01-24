package com.example.movies.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> mMovies;
    private int mCellWidthPixels;
    private int mCellHeightPixels;

    public MoviesListAdapter(List<Movie> movies, int cellWidthPixels, int cellHeightPixels) {
        mMovies = new ArrayList<>(movies);
        mCellWidthPixels = cellWidthPixels;
        mCellHeightPixels = cellHeightPixels;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view_holder, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = mCellWidthPixels;
        layoutParams.height = mCellHeightPixels;
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.setMovie(mMovies.get(position), mCellWidthPixels);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
