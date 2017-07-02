package com.example.movies.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

public class CenterInsideDrawable extends Drawable {

    private int mFullWidth;
    private int mFullHeight;
    private Drawable mDrawable;

    public CenterInsideDrawable(int fullWidth, int fullHeight, Drawable drawable) {
        mFullWidth = fullWidth;
        mFullHeight = fullHeight;
        mDrawable = drawable;
    }

    @Override
    public int getIntrinsicWidth() {
        return mFullWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mFullHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        int marginX = (mFullWidth - mDrawable.getIntrinsicWidth()) / 2;
        int marginY = (mFullHeight - mDrawable.getIntrinsicHeight()) / 2;

        mDrawable.setBounds(marginX, marginY, marginX + mDrawable.getIntrinsicWidth(), marginY + mDrawable.getIntrinsicHeight());
        mDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }
}
