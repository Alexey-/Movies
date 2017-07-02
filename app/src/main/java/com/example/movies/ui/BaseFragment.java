package com.example.movies.ui;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public abstract String getTitle();

    private Snackbar mSnackbar;

    protected void showMessage(@StringRes int messageRes) {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
        mSnackbar = Snackbar.make(getView(), messageRes, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

}
