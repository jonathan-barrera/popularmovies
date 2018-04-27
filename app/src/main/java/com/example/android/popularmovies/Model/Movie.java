package com.example.android.popularmovies.Model;

/**
 * Created by jonathanbarrera on 4/12/18.
 * Movie object contains information about the poster image and MovieDatabase ID for a movie
 */

public class Movie {

    // Movie Poster image link
    private String mPosterLink;

    // TMDb ID
    private String mMovieDatabaseID;

    // Pass in the movie information into the constructor
    public Movie (String movieDatabaseID, String posterLink){
        mMovieDatabaseID = movieDatabaseID;
        mPosterLink = posterLink;
    }

    // Methods for retrieving the information:

    public String getPosterLink() {
        return mPosterLink;
    }

    public String getMovieDatabaseID() { return mMovieDatabaseID; }
}
