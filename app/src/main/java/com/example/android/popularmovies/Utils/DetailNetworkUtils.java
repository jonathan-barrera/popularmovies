package com.example.android.popularmovies.Utils;

import com.example.android.popularmovies.BuildConfig;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by jonathanbarrera on 4/25/18.
 */

public class DetailNetworkUtils {
    // Create static final strings for URL building
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAM = "api_key";

    // Reviewer/user needs to use his/her own api key
    private static final String API_KEY = BuildConfig.API_KEY;

    // Build URL based on the preference input
    private static String buildUrl(String movieDatabaseId) {
        // Build the URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();

        // Add the movie database ID for the movie
        urlBuilder.addPathSegment(movieDatabaseId);

        // Add parameters
        urlBuilder.addQueryParameter(API_KEY_PARAM, API_KEY);

        // Convert to String and return (add the append at the end to include information
        // on the trailer links and reviews
        return urlBuilder.build().toString() + "&append_to_response=videos,reviews";
    }

    // Get the JSON string from the web API using OkHttp library
    public static String getResponseFromHttpUrl(String movieDatabaseId) throws IOException {
        String url = buildUrl(movieDatabaseId);
        OkHttpClient client = new OkHttpClient();

        if (url == null) {
            return null;
        }

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
