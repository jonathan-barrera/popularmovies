package com.example.android.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jonathanbarrera on 4/12/18.
 * Movie object contains information about a single movie
 */

public class Movie implements Parcelable {

    // Movie title
    private String mTitle;

    // Movie Poster image link
    private String mImageLink;

    // Movie synopsis
    private String mSynopsis;

    // Movie average rating
    private float mRating;

    // Movie release data
    private String mReleaseDate;

    // Pass in the movie information into the constructor
    public Movie (String title, String imageLink, String synopsis, float rating,
                  String releaseDate){
        mTitle = title;
        mImageLink = imageLink;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    // Methods for retrieving the information:
    public String getMovieTitle() {
        return mTitle;
    }

    public String getImageLink() {
        return mImageLink;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mImageLink);
        parcel.writeString(mSynopsis);
        parcel.writeFloat(mRating);
        parcel.writeString(mReleaseDate);
    }

    private Movie (Parcel in) {
        mTitle = in.readString();
        mImageLink = in.readString();
        mSynopsis = in.readString();
        mRating = in.readFloat();
        mReleaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR =
            new Creator<Movie>() {
                @Override
                public Movie createFromParcel(Parcel parcel) {
                    return new Movie(parcel);
                }

                @Override
                public Movie[] newArray(int i) {
                    return new Movie[i];
                }
            };
}
