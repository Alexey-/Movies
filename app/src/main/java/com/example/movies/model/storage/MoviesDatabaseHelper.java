package com.example.movies.model.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.movies.utils.Log;

public class MoviesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int VERSION = 3;

    public MoviesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    public void createTables(SQLiteDatabase db) {
        createMoviesTables(db);
        createVideosTables(db);
        createReviewsTables(db);
    }

    public void createMoviesTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MoviesContract.MoviesTable.TABLE_NAME + " (" +
                MoviesContract.MoviesTable._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MoviesTable.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesTable.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MoviesTable.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesTable.COLUMN_VOTE_AVERAGE + " REAL, " +
                MoviesContract.MoviesTable.COLUMN_RELEASE_DATE + " INTEGER)"
        );
        db.execSQL("CREATE TABLE " + MoviesContract.MovieListTypeCrossTable.TABLE_NAME + " (" +
                MoviesContract.MovieListTypeCrossTable.COLUMN_LIST_TYPE + " TEXT NOT NULL, " +
                MoviesContract.MovieListTypeCrossTable.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.MovieListTypeCrossTable.COLUMN_SORT_ORDER + " INTEGER NOT NULL)"
        );
    }

    public void createVideosTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MoviesContract.VideosTable.TABLE_NAME + " (" +
                MoviesContract.VideosTable._ID + " TEXT PRIMARY KEY, " +
                MoviesContract.VideosTable.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.VideosTable.COLUMN_KEY + " TEXT NOT NULL, " +
                MoviesContract.VideosTable.COLUMN_NAME + " TEXT, " +
                MoviesContract.VideosTable.COLUMN_TYPE + " TEXT NOT NULL, " +
                MoviesContract.VideosTable.COLUMN_SORT_ID + " INTEGER NOT NULL)"
        );
    }

    public void createReviewsTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MoviesContract.ReviewsTable.TABLE_NAME + " (" +
                MoviesContract.ReviewsTable._ID + " TEXT PRIMARY KEY, " +
                MoviesContract.ReviewsTable.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewsTable.COLUMN_AUTHOR + " TEXT, " +
                MoviesContract.ReviewsTable.COLUMN_CONTENT + " TEXT, " +
                MoviesContract.ReviewsTable.COLUMN_URL + " TEXT, " +
                MoviesContract.ReviewsTable.COLUMN_SORT_ID + " INTEGER NOT NULL)"
        );
    }

    public void deleteTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieListTypeCrossTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.VideosTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsTable.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            createVideosTables(db);
            oldVersion++;
        }
        if (oldVersion == 2) {
            createReviewsTables(db);
            oldVersion++;
        }
    }

    private static final String LOG_TAG = "MoviesDatabase";

    private void printCursor(Cursor cursor) {
        int columnCount = cursor.getColumnCount();

        StringBuilder header = new StringBuilder();
        for (int i = 0; i < columnCount; ++i) {
            header.append(String.format("%20s", cursor.getColumnName(i)));
        }

        Log.v(LOG_TAG, "-----------");
        Log.v(LOG_TAG, header.toString());
        Log.v(LOG_TAG, "-----------");

        while (cursor.moveToNext()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < columnCount; ++i) {
                row.append(String.format("%20s", cursor.getString(i)));
            }
            Log.v(LOG_TAG, row.toString());
        }

        Log.v(LOG_TAG, "-----------");

        cursor.close();
    }

    public void printContent() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + MoviesContract.MoviesTable.TABLE_NAME, null);
        printCursor(cursor);
        cursor = database.rawQuery("SELECT * FROM " + MoviesContract.MovieListTypeCrossTable.TABLE_NAME, null);
        printCursor(cursor);
        cursor = database.rawQuery("SELECT * FROM " + MoviesContract.VideosTable.TABLE_NAME, null);
        printCursor(cursor);
    }
}
