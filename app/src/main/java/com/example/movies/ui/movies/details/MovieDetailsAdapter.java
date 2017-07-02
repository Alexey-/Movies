package com.example.movies.ui.movies.details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.model.Movie;
import com.example.movies.model.Video;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_VIEW_TYPE_MOVIE_DESCRIPTION = 0;
    public static final int ITEM_VIEW_TYPE_VIDEO_TYPE = 1;
    public static final int ITEM_VIEW_TYPE_VIDEO = 2;

    private Movie mMovie;
    private List<Object> mVideosCombined;

    public MovieDetailsAdapter(Movie movie) {
        mMovie = movie;
        mVideosCombined = new ArrayList<>();
    }

    public void setVideos(List<Video> videos) {
        mVideosCombined.clear();
        if (videos != null) {
            for (Video.Type type : Video.Type.values()) {
                boolean headerInserted = false;
                for (Video video : videos) {
                    if (video.getType() == type) {
                        if (!headerInserted) {
                            headerInserted = true;
                            mVideosCombined.add(type);
                        }
                        mVideosCombined.add(video);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_MOVIE_DESCRIPTION;
        } else {
            if (mVideosCombined.get(position - 1) instanceof Video.Type) {
                return ITEM_VIEW_TYPE_VIDEO_TYPE;
            } else {
                return ITEM_VIEW_TYPE_VIDEO;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_MOVIE_DESCRIPTION:
                return new MovieDescriptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_description_view_holder, parent, false));
            case ITEM_VIEW_TYPE_VIDEO_TYPE:
                return new VideoTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_type_view_holder, parent, false));
            case ITEM_VIEW_TYPE_VIDEO:
                return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view_holder, parent, false));
            default:
                throw new RuntimeException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieDescriptionViewHolder) {
            ((MovieDescriptionViewHolder) holder).setMovie(mMovie);
        } else if (holder instanceof VideoTypeViewHolder) {
            ((VideoTypeViewHolder) holder).setVideoType((Video.Type) mVideosCombined.get(position - 1));
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).setVideo((Video) mVideosCombined.get(position - 1));
        } else {
            throw new RuntimeException("Unknown holder type: " + holder.getClass().getSimpleName());
        }
    }

    @Override
    public int getItemCount() {
        return 1 + mVideosCombined.size();
    }

}
