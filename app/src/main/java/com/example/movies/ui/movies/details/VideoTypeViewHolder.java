package com.example.movies.ui.movies.details;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.movies.databinding.VideoTypeViewHolderBinding;
import com.example.movies.model.Video;

public class VideoTypeViewHolder extends RecyclerView.ViewHolder {

    private Video.Type mVideoType;

    private VideoTypeViewHolderBinding mBinding;

    public VideoTypeViewHolder(View itemView) {
        super(itemView);
        mBinding = VideoTypeViewHolderBinding.bind(itemView);
    }

    public void setVideoType(Video.Type type) {
        mVideoType = type;
        mBinding.title.setText(type.getTitleRes());
    }

}
