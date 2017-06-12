package com.example.movies.model.storage;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.movies.model.MoviesListType;

import static com.example.movies.model.storage.MoviesContract.AUTHORITY;
import static com.example.movies.model.storage.MoviesContract.FAVORITES_MOVIES_URI;
import static com.example.movies.model.storage.MoviesContract.MovieListTypeCrossTable;
import static com.example.movies.model.storage.MoviesContract.MoviesTable;
import static com.example.movies.model.storage.MoviesContract.PATH_MOVIES_BASE;
import static com.example.movies.model.storage.MoviesContract.PATH_MOVIES_FAVORITES;
import static com.example.movies.model.storage.MoviesContract.PATH_MOVIES_MOST_POPULAR;
import static com.example.movies.model.storage.MoviesContract.PATH_MOVIES_TOP_RATED;

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int MOVIES_MOST_POPULAR = 102;
    public static final int MOVIES_TOP_RATED = 103;
    public static final int MOVIES_FAVORITES = 104;
    public static final int MOVIES_FAVORITE_WITH_ID = 105;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE, MOVIES);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE + "/" + PATH_MOVIES_MOST_POPULAR, MOVIES_MOST_POPULAR);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE + "/" + PATH_MOVIES_TOP_RATED, MOVIES_TOP_RATED);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE + "/" + PATH_MOVIES_FAVORITES, MOVIES_FAVORITES);
        uriMatcher.addURI(AUTHORITY, PATH_MOVIES_BASE + "/" + PATH_MOVIES_FAVORITES + "/#", MOVIES_FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private MoviesDatabaseHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MoviesDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (projection != null || selection != null || selectionArgs != null || sortOrder != null) {
            throw new IllegalArgumentException("Query arguments are not supported");
        }

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor result = null;

        MoviesListType typeToQuery = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                result = db.query(MoviesTable.TABLE_NAME, null, null, null, null, null, null);
                break;
            case MOVIE_WITH_ID:
            case MOVIES_FAVORITE_WITH_ID:
                result = db.query(MoviesTable.TABLE_NAME, null, MoviesTable._ID + " = ?", new String[] { uri.getLastPathSegment() }, null, null, null);
                break;
            case MOVIES_MOST_POPULAR:
                typeToQuery = MoviesListType.MOST_POPULAR;
                break;
            case MOVIES_TOP_RATED:
                typeToQuery = MoviesListType.TOP_RATED;
                break;
            case MOVIES_FAVORITES:
                typeToQuery = MoviesListType.FAVORITES;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri for query: " + uri);
        }

        if (typeToQuery != null) {
            result = db.rawQuery("SELECT " + MoviesTable.TABLE_NAME + ".* " +
                    "FROM " + MoviesTable.TABLE_NAME + " join " + MovieListTypeCrossTable.TABLE_NAME + " on " +
                    MoviesTable._ID + " = " + MovieListTypeCrossTable.COLUMN_MOVIE_ID +
                    " WHERE " + MovieListTypeCrossTable.COLUMN_LIST_TYPE + " = ? " +
                    " ORDER BY " + MovieListTypeCrossTable.COLUMN_SORT_ORDER
                    , new String[] { typeToQuery.toString() });
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Uri result = null;
        try {
            db.beginTransaction();

            switch (sUriMatcher.match(uri)) {
                case MOVIES_FAVORITES:
                    String movieId = values.getAsString(MoviesTable._ID);

                    Cursor maxSortOrderCursor = db.rawQuery("SELECT max(" + MovieListTypeCrossTable.COLUMN_SORT_ORDER + ")" +
                            " FROM " + MovieListTypeCrossTable.TABLE_NAME +
                            " WHERE " + MovieListTypeCrossTable.COLUMN_LIST_TYPE + " = \"" + MoviesListType.FAVORITES +  "\"", null);
                    maxSortOrderCursor.moveToNext();
                    int maxSortOrder = maxSortOrderCursor.getInt(0) + 1;
                    maxSortOrderCursor.close();

                    db.execSQL("INSERT INTO " + MovieListTypeCrossTable.TABLE_NAME + " VALUES (" +
                            "\"" + MoviesListType.FAVORITES +"\", " +
                            "\"" + movieId + "\", " +
                            "(" + maxSortOrder + "))");
                    result = FAVORITES_MOVIES_URI;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uri for insert: " + uri);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (result != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();

            MoviesListType typeToQuery = null;

            switch (sUriMatcher.match(uri)) {
                case MOVIES_MOST_POPULAR:
                    typeToQuery = MoviesListType.MOST_POPULAR;
                    break;
                case MOVIES_TOP_RATED:
                    typeToQuery = MoviesListType.TOP_RATED;
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uri for bulk insert: " + uri);
            }

            if (typeToQuery != null) {
                ContentValues crossValues = new ContentValues();
                db.execSQL("DELETE FROM " + MovieListTypeCrossTable.TABLE_NAME + " WHERE " + MovieListTypeCrossTable.COLUMN_LIST_TYPE + " = \"" + typeToQuery + "\"");
                for (int i = 0; i < values.length; ++i) {
                    db.replace(MoviesTable.TABLE_NAME, null, values[i]);

                    crossValues.clear();
                    crossValues.put(MovieListTypeCrossTable.COLUMN_LIST_TYPE, typeToQuery.toString());
                    crossValues.put(MovieListTypeCrossTable.COLUMN_MOVIE_ID, values[i].getAsString(MoviesTable._ID));
                    crossValues.put(MovieListTypeCrossTable.COLUMN_SORT_ORDER, i);
                    db.insert(MovieListTypeCrossTable.TABLE_NAME, null, crossValues);

                    result++;
                }
            }

            cleanupOrphanMovies(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (selection != null || selectionArgs != null) {
            throw new IllegalArgumentException("Delete arguments are not supported");
        }

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();

            switch (sUriMatcher.match(uri)) {
                case MOVIES_FAVORITE_WITH_ID:
                    result = db.delete(MovieListTypeCrossTable.TABLE_NAME,
                            MovieListTypeCrossTable.COLUMN_MOVIE_ID + " = ? AND " + MovieListTypeCrossTable.COLUMN_LIST_TYPE + " = ?",
                            new String[] { uri.getLastPathSegment(), MoviesListType.FAVORITES.toString() });
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uri for delete: " + uri);
            }

            cleanupOrphanMovies(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (result != 0) {
            getContext().getContentResolver().notifyChange(FAVORITES_MOVIES_URI, null);
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (selection != null || selectionArgs != null) {
            throw new IllegalArgumentException("Delete arguments are not supported");
        }

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();

            switch (sUriMatcher.match(uri)) {
                case MOVIE_WITH_ID:
                case MOVIES_FAVORITE_WITH_ID:
                    if (db.replace(MoviesTable.TABLE_NAME, null, values) != -1) {
                        result++;
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uri for update: " + uri);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (result != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return result;
    }

    private void cleanupOrphanMovies(SQLiteDatabase db) {
        String boundMovies = "(SELECT DISTINCT(" + MovieListTypeCrossTable.COLUMN_MOVIE_ID + ") FROM " + MovieListTypeCrossTable.TABLE_NAME + ")";
        db.execSQL("DELETE FROM " + MoviesTable.TABLE_NAME + " WHERE " + MoviesTable._ID + " NOT IN " + boundMovies);
    }

}
