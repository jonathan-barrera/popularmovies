package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.BuildConfig;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
/**
 * Created by jonathanbarrera on 4/12/18.
 * Utility functions used to communicate with the server
 */

public class NetworkUtils {
    // Create static final strings for URL building
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String POPULAR_PATH_KEY = "Popular";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH_KEY = "Top Rated";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String API_KEY_PARAM = "api_key";

    // Reviewer/user needs to use his/her own api key
    private static final String API_KEY = BuildConfig.API_KEY;

    // Build URL based on the preference input
    public static String buildUrl(String preference) {
        // Build the URL
        HttpUrl.Builder urlBuilder;
        switch (preference){
            // Decide whether to sort by popularity or by highest rated.
            case POPULAR_PATH_KEY:
                urlBuilder = HttpUrl.parse(BASE_URL + POPULAR_PATH).newBuilder();
                break;
            case TOP_RATED_PATH_KEY:
                urlBuilder = HttpUrl.parse(BASE_URL + TOP_RATED_PATH).newBuilder();
                break;
            default:
                // Return early if neither preference is input.
                return null;
        }

        // Add parameters
        urlBuilder.addQueryParameter(API_KEY_PARAM, API_KEY);

        // Convert to String and return
        return urlBuilder.build().toString();
    }

    // Get the JSON string from the web API using OkHttp library
    public static String getResponseFromHttpUrl(String preference) throws IOException {
        String url = buildUrl(preference);
        OkHttpClient client = new OkHttpClient();

        if (url == null) {
            return null;
        }

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}