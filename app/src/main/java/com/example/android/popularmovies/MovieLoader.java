package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Utils.MovieJsonUtils;
import com.example.android.popularmovies.Utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by jonathanbarrera on 4/13/18.
 * AsyncTaskLoader class to perform the HTTP request in the background
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    // Declare global variable for user's preference
    private String mPreference;

    // Constructor
    public MovieLoader(Context context, String preference) {
        super(context);
        mPreference = preference;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        // Don't perform task if preference is empty
        if (mPreference == null) {
            return null;
        }

        // Perform the HTTP Request for movie data
        String jsonResponseString;
        try {
            jsonResponseString = NetworkUtils.getResponseFromHttpUrl(mPreference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Extract movie information from the JSONstring and convert to a list of movie objects
        List<Movie> movies;
        try {
            movies = MovieJsonUtils.extractMovies(jsonResponseString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return movies;
    }
}
