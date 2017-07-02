package com.example.movies.ui;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.movies.R;
import com.example.movies.databinding.MainActivityBinding;
import com.example.movies.model.Movie;
import com.example.movies.model.MoviesListType;
import com.example.movies.ui.movies.details.EmptyDetailsFragment;
import com.example.movies.ui.movies.details.MovieDetailsFragment;
import com.example.movies.ui.movies.list.MoviesListFragment;
import com.example.movies.ui.movies.list.OnMovieSelectedListener;

public class MainActivity extends AppCompatActivity
        implements OnMovieSelectedListener, NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private boolean mTablet;

    private MainActivityBinding mBinding;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;
    private boolean mSideMenuEnabled = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        mTablet = getResources().getBoolean(R.bool.is_tablet);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mBinding.drawer, mBinding.toolbar, R.string.open_menu, R.string.close_menu);
        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBinding.drawer.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            displayMoviesList(MoviesListType.MOST_POPULAR);
        }

        mBinding.navigationMenu.setNavigationItemSelectedListener(this);

        mFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(FragmentManager fragmentManager, Fragment fragment) {
                if (fragment instanceof BaseFragment) {
                    BaseFragment baseFragment = (BaseFragment) fragment;
                    if (mTablet) {
                        if (baseFragment.getId() == R.id.left_pane_container) {
                            setTitle(baseFragment.getTitle());
                        }
                    } else {
                        setTitle(baseFragment.getTitle());
                    }
                }
            }
        };
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, false);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        MovieDetailsFragment fragment = MovieDetailsFragment.createFragment(movie);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mTablet) {
            transaction.replace(R.id.right_pane_container, fragment);
        } else {
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            transaction.replace(R.id.fullscreen_container, fragment);
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void displayMoviesList(MoviesListType type) {
        MoviesListFragment fragment = MoviesListFragment.createFragment(type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mTablet) {
            transaction.replace(R.id.right_pane_container, EmptyDetailsFragment.createFragment());
            transaction.replace(R.id.left_pane_container, fragment);
        } else {
            transaction.replace(R.id.fullscreen_container, fragment);
        }
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                displayMoviesList(MoviesListType.MOST_POPULAR);
                break;
            case R.id.top_rated:
                displayMoviesList(MoviesListType.TOP_RATED);
                break;
            case R.id.favorites:
                displayMoviesList(MoviesListType.FAVORITES);
                break;
        }
        mBinding.drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        setMenuEnabled(getSupportFragmentManager().getBackStackEntryCount() == 0);
    }

    private void setMenuEnabled(boolean enabled) {
        if (mSideMenuEnabled != enabled) {
            if (enabled) {
                mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            } else {
                mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            mSideMenuEnabled = enabled;
        }
    }
}
