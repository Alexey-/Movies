package com.example.movies.ui.movies.review;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.movies.R;
import com.example.movies.databinding.ReviewViewHolderBinding;
import com.example.movies.model.Review;
import com.example.movies.utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>{

    private List<Review> mReviews;

    public ReviewsAdapter() {
        mReviews = new ArrayList<>();
    }

    public void setReviews(List<Review> reviews) {
        if (reviews == null) {
            mReviews = Collections.emptyList();
        } else {
            mReviews = new ArrayList<>(reviews);
        }
        notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_view_holder, parent, false);
        return new ReviewsAdapter.ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewViewHolder holder, int position) {
        holder.setReview(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        private Review mReview;

        private ReviewViewHolderBinding mBinding;

        public ReviewViewHolder(final View itemView) {
            super(itemView);
            mBinding = ReviewViewHolderBinding.bind(itemView);

            mBinding.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(view.getContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_review, popup.getMenu());
                    popup.setOnMenuItemClickListener(ReviewViewHolder.this);
                    popup.show();
                }
            });
        }

        public void setReview(Review review) {
            mReview = review;
            if (TextUtils.isEmpty(review.getAuthor())) {
                mBinding.author.setVisibility(View.GONE);
            } else {
                mBinding.author.setVisibility(View.VISIBLE);
                mBinding.author.setText(review.getAuthor());
            }
            if (TextUtils.isEmpty(review.getContent())) {
                mBinding.content.setVisibility(View.GONE);
            } else {
                mBinding.content.setVisibility(View.VISIBLE);
                mBinding.content.setText(review.getContent());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.show_original: {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(mReview.getUrl()));
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e(Log.DEFAULT_TAG, "Failed to open review", e);
                        Snackbar.make(itemView.getRootView(), R.string.reviews_cannot_open, Snackbar.LENGTH_LONG).show();
                    }
                }   return true;
            }
            return false;
        }
    }

}
