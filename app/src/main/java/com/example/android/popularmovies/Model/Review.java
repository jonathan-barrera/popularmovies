package com.example.android.popularmovies.Model;

/**
 * Created by jonathanbarrera on 4/27/18.
 * Review object contains information about the reviews for a specific movie
 */

public class Review {
    // Review Content
    private String mReviewContent;

    // Review Author
    private String mReviewAuthor;

    // Constructor
    public Review(String reviewContent, String reviewAuthor){
        mReviewContent = "\"" + reviewContent + "\"";
        mReviewAuthor = reviewAuthor;
    }

    // Methods for getting the review information
    public String getReviewContent() {
        return mReviewContent;
    }

    public String getReviewAuthor() {
        return mReviewAuthor;
    }
}
