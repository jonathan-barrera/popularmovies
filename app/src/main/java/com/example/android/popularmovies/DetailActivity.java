package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/w500/";

    // Bind views to layout views
    @BindView(R.id.detail_movie_image_view)
    ImageView mDetailImageView;
    @BindView(R.id.detail_rating_bar)
    RatingBar mDetailRatingBar;
    @BindView(R.id.detail_release_date_text_view)
    TextView mDetailReleaseDateView;
    @BindView(R.id.detail_synopsis_text_view)
    TextView mDetailSynopsisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Bind views
        ButterKnife.bind(this);

        // Get the intent that called this activity
        Intent intent = getIntent();

        // If the intent is null, close the detail activity
        if (intent == null) {
            closeOnError();
        }

        // Get Movie object from the intent
        Movie movie;
        try {
            movie = intent.getParcelableExtra(getString(R.string.movie_key));

            // Set the Title
            setTitle(movie.getMovieTitle());

            // Set the movie data to the views in the DetailActivity
            Picasso.with(this)
                    .load(BASE_URL + movie.getImageLink())
                    .into(mDetailImageView);
            mDetailRatingBar.setRating(movie.getRating());
            mDetailReleaseDateView.setText(movie.getReleaseDate());
            mDetailSynopsisView.setText(movie.getSynopsis());
        } catch (NullPointerException e) {
            // If there is an error getting the Extra data, close early
            closeOnError();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message_detail, Toast.LENGTH_SHORT).show();
    }


}
