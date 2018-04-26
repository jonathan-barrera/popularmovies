package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by jonathanbarrera on 4/25/18.
 * The content provider for Popular Movies Favorites data
 */

public class FavoritesProvider extends ContentProvider {

    // Codes for matching
    public static final int CODE_FAVORITES = 100;

    // Declare a URI matcher using the buildUriMatcher method
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Declare a member DbHelper variable
    private FavoritesDbHelper mDbHelper;

    // Method for building the UriMatcher
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(FavoritesContract.CONTENT_AUTHORITY, FavoritesContract.PATH_FAVORITES,
                CODE_FAVORITES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        // Initialize the DBHelper
        mDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String orderBy) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            // Query for all entries
            // There is no need to query for one favorite entry at the moment
            case CODE_FAVORITES:
                cursor = mDbHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // Declare returnUri variable
        Uri returnUri;

        switch(sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                long newRowId = mDbHelper.getWritableDatabase().insert(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        null,
                        contentValues
                );

                if (newRowId > 0) {
                    returnUri = ContentUris.withAppendedId(
                            FavoritesContract.FavoritesEntry.CONTENT_URI, newRowId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify change
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Count the number of rows deleted
        int rowsDeleted;

        switch(sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                rowsDeleted = mDbHelper.getWritableDatabase().delete(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                if (rowsDeleted <= 0) {
                    throw new SQLException("Failed to delete row: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver of the changes
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
