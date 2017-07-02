package com.example.movies.ui.movies.details;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.movies.databinding.VideoViewHolderBinding;
import com.example.movies.model.Video;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private Video mVideo;

    private VideoViewHolderBinding mBinding;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mBinding = VideoViewHolderBinding.bind(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(mVideo.getTrailerUri());
                v.getContext().startActivity(intent);
            }
        });
    }

    public void setVideo(Video video) {
        mVideo = video;
        mBinding.title.setText(video.getName());
    }

}
