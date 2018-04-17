package com.example.android.popularmovies.Utils;

import android.util.Log;

import com.example.android.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanbarrera on 4/12/18.
 * Utility functions to handle Movie Database JSON information.
 */
public class MovieJsonUtils {

    private MovieJsonUtils() {}

    /**
     * Return a list of Movie objects that have been built form the JSON response data
     */
    public static List<Movie> extractMovies(String jsonResponseString) throws JSONException {
        // final strings for JSON keys
        final String JSON_KEY_RESULTS = "results";
        final String JSON_KEY_TITLE = "title";
        final String JSON_KEY_RATING = "vote_average";
        final String JSON_KEY_SYNOPSIS = "overview";
        final String JSON_KEY_RELEASE_DATE = "release_date";
        final String JSON_KEY_IMAGE_LINK = "poster_path";

        final String JSON_KEY_STATUS_MESSAGE = "status_message";

        // Create empty list to start adding Movie objects too
        List<Movie> moviesList = new ArrayList<>();

        // Create JSON object from the response string
        JSONObject moviesData = new JSONObject(jsonResponseString);

        // Check for errors -- if it has an error, return null
        if (moviesData.has(JSON_KEY_STATUS_MESSAGE)) {
            return null;
        }

        // Extract Array of movies
        JSONArray movieResults = moviesData.optJSONArray(JSON_KEY_RESULTS);

        for (int i = 0; i < movieResults.length(); i++) {
            // Extract the current movie jsonobject
            JSONObject currentMovieData = movieResults.getJSONObject(i);

            // Extract all the relevant information from the movie jsonobject
            String title = currentMovieData.optString(JSON_KEY_TITLE);
            float rating = (float) currentMovieData.optDouble(JSON_KEY_RATING);
            String synopsis = currentMovieData.optString(JSON_KEY_SYNOPSIS);
            String releaseDate = currentMovieData.optString(JSON_KEY_RELEASE_DATE);
            String imageLink = currentMovieData.optString(JSON_KEY_IMAGE_LINK);

            // Create new Movie object with the above information
            Movie currentMovie = new Movie(title, imageLink, synopsis, rating, releaseDate);

            // Add to movies list
            moviesList.add(currentMovie);
        }

        return moviesList;
    }
}
