package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Model.DetailMovie;
import com.example.android.popularmovies.Utils.DetailMovieJsonUtils;
import com.example.android.popularmovies.Utils.DetailNetworkUtils;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by jonathanbarrera on 4/25/18.
 * Loader for DetailgitMovie object
 */

public class DetailMovieLoader extends AsyncTaskLoader<DetailMovie> {

    // Declare global variable for the movie database info sent in the intent from the main activity
    private String mDatabaseId;

    // Constructor
    public DetailMovieLoader(Context context, String databaseId) {
        super(context);
        mDatabaseId = databaseId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public DetailMovie loadInBackground() {
        // Don't perform task if database id is empty
        if (mDatabaseId == null || mDatabaseId.equals("")) {
            return null;
        }

        // Perform the HTTP Request for movie data
        String jsonResponseString;
        try {
            jsonResponseString = DetailNetworkUtils.getResponseFromHttpUrl(mDatabaseId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Extract movie information from the JSONstring and convert to a list of movie objects
        DetailMovie detailMovie;
        try {
            detailMovie = DetailMovieJsonUtils.extractDetailMovie(jsonResponseString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return detailMovie;
    }
}