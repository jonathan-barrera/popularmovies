package com.example.android.popularmovies.Model;

/**
 * Created by jonathanbarrera on 4/25/18.
 * Detail Movie object contains all the information about a single movie
 */

public class DetailMovie {
    // Movie title
    private String mTitle;

    // Movie synopsis
    private String mSynopsis;

    // Movie average rating
    private float mRating;

    // Movie release data
    private String mReleaseDate;

    // Movie Backdrop image link
    private String mBackdropLink;

    // Trailer video link
    private String[] mTrailerLinks;

    // Reviews
    private String[] mReviews;

    // Review Authors
    private String[] mReviewAuthors;

    // TMDb ID
    private String mMovieDatabaseID;

    // Poster Link
    private String mPosterLink;

    // Pass in the movie information into the constructor
    public DetailMovie (String title, String synopsis, float rating,
                  String releaseDate, String backdropLink, String[] trailerLinks,
                  String[] reviews, String[] reviewAuthors, String movieDatabaseID,
                        String posterLink){
        mTitle = title;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
        mBackdropLink = backdropLink;
        mTrailerLinks = trailerLinks;
        mReviews = reviews;
        mReviewAuthors = reviewAuthors;
        mMovieDatabaseID = movieDatabaseID;
        mPosterLink = posterLink;
    }

    // Methods for retrieving the information:
    public String getMovieTitle() {
        return mTitle;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public float getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getBackdropLink() { return mBackdropLink; }

    public String[] getTrailerLinks() { return mTrailerLinks; }

    public String[] getReviews() { return mReviews; }

    public String[] getReviewAuthors() { return mReviewAuthors; }

    public String getMovieDatabaseID() { return mMovieDatabaseID; }

    public String getPosterLink() { return mPosterLink; }
}
