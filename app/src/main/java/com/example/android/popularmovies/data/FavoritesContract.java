package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jonathanbarrera on 4/25/18.
 * Table and column names for the Movie Favorites database
 */

public class FavoritesContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {

        // Content uri used for querying the favorites table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        // Labels for the table name and title column
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_DATABASE_ID = "movieDatabaseId";
        public static final String COLUMN_POSTER_LINK = "posterLink";
    }

}
