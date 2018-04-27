package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.Model.DetailMovie;
import com.example.android.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanbarrera on 4/25/18.
 */

public class DetailMovieJsonUtils {

    private DetailMovieJsonUtils() {}

    /**
     * Return a list of Movie objects that have been built form the JSON response data
     */
    public static DetailMovie extractDetailMovie(String jsonResponseString) throws JSONException {
        // final strings for JSON keys
        final String JSON_KEY_TITLE = "title";
        final String JSON_KEY_RATING = "vote_average";
        final String JSON_KEY_SYNOPSIS = "overview";
        final String JSON_KEY_RELEASE_DATE = "release_date";
        final String JSON_KEY_BACKDROP_LINK = "backdrop_path";
        final String JSON_KEY_VIDEOS = "videos";
        final String JSON_KEY_RESULTS = "results";
        final String JSON_KEY_TRAILER_LINK = "key";
        final String JSON_KEY_REVIEWS = "reviews";
        final String JSON_KEY_REVIEW_CONTENT = "content";
        final String JSON_KEY_DATABASE_ID = "id";
        final String JSON_KEY_POSTER_LINK = "poster_path";

        final String JSON_KEY_STATUS_MESSAGE = "status_message";


        // Create JSON object from the response string
        JSONObject currentMovieData = new JSONObject(jsonResponseString);

        // Check for errors -- if it has an error, return null
        if (currentMovieData.has(JSON_KEY_STATUS_MESSAGE)) {
            return null;
        }

        // Extract all the relevant information from the movie jsonobject
        String title = currentMovieData.optString(JSON_KEY_TITLE);
        float rating = (float) currentMovieData.optDouble(JSON_KEY_RATING);
        String synopsis = currentMovieData.optString(JSON_KEY_SYNOPSIS);
        String releaseDate = currentMovieData.optString(JSON_KEY_RELEASE_DATE);
        String backdropLink = currentMovieData.optString(JSON_KEY_BACKDROP_LINK);
        String databaseId = String.valueOf(currentMovieData.optInt(JSON_KEY_DATABASE_ID));
        String posterLink = currentMovieData.optString(JSON_KEY_POSTER_LINK);

        // Extract the trailer link information for this movie (if it exists)
        JSONObject currentMovieTrailers = currentMovieData.optJSONObject(JSON_KEY_VIDEOS);
        JSONArray trailerResults = currentMovieTrailers.optJSONArray(JSON_KEY_RESULTS);
        String trailerLink;
        if (trailerResults.length() > 0) {
            trailerLink = trailerResults.optJSONObject(0).optString(JSON_KEY_TRAILER_LINK);
        } else {
            trailerLink = "";
        }

        // Extract the reviews for this movie (if they exist)
        JSONObject currentMovieReviews = currentMovieData.optJSONObject(JSON_KEY_REVIEWS);
        JSONArray reviewsResults = currentMovieReviews.optJSONArray(JSON_KEY_RESULTS);
        String[] reviews;
        if (reviewsResults.length() > 0) {
            reviews = new String[reviewsResults.length()];
            for (int i = 0; i < reviewsResults.length(); i++) {
                String currentReview = reviewsResults.optJSONObject(i)
                        .optString(JSON_KEY_REVIEW_CONTENT);
                reviews[i] = currentReview;
            }
        } else {
            reviews = new String[0];
        }

        // Create new Movie object with the above information and return
        return new DetailMovie(title, synopsis, rating, releaseDate,
                backdropLink, trailerLink, reviews, databaseId, posterLink);
    }
}
