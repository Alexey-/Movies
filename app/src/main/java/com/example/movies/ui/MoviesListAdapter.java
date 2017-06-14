package com.example.movies.ui;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> mMovies;
    private int mCellWidthPixels;
    private int mCellHeightPixels;

    public MoviesListAdapter(int cellWidthPixels, int cellHeightPixels) {
        mMovies = Collections.emptyList();
        mCellWidthPixels = cellWidthPixels;
        mCellHeightPixels = cellHeightPixels;
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
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
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
