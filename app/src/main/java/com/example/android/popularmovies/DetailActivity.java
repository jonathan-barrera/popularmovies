package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Model.DetailMovie;
import com.example.android.popularmovies.data.FavoritesContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<DetailMovie> {

    private static final String BASE_URL = "https://image.tmdb.org/t/p/w500/";
    private static final int DETAIL_LOADER_ID = 2;
    private static final String YOUTUBE_WEB_BASE = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_BASE = "vnd.youtube:";

    private static boolean isAlreadyFavorite = false;

    private Cursor mCursor;

    // Bind views to layout views
    @BindView(R.id.detail_movie_image_view)
    ImageView mDetailImageView;
    @BindView(R.id.detail_rating_bar)
    RatingBar mDetailRatingBar;
    @BindView(R.id.detail_release_date_text_view)
    TextView mDetailReleaseDateView;
    @BindView(R.id.detail_synopsis_text_view)
    TextView mDetailSynopsisView;
    @BindView(R.id.no_trailers_found_text_view)
    TextView mNoTrailersFound;
    @BindView(R.id.no_reviews_found_text_view)
    TextView mNoReviewsFound;

    private String mMovieDatabaseId;
    private DetailMovie mDetailMovie;

    private MenuItem mFavoriteOption;
    private MenuItem mUnfavoriteOption;

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

        try {
            mMovieDatabaseId = intent.getStringExtra(
                    FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID);
        } catch (NullPointerException e) {
            closeOnError();
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(DETAIL_LOADER_ID, null, this);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message_detail, Toast.LENGTH_SHORT).show();
    }

    private void checkFavoriteStatus() {

        String[] projection = new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID};
        String selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID;
        String[] selectionArgs = new String[]{mDetailMovie.getMovieDatabaseID()};

        mCursor = getContentResolver().query(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                projection,
                selection + "=?",
                selectionArgs,
                null
        );

        if (mCursor.getCount() == 0) {
            isAlreadyFavorite = false;
        } else {
            isAlreadyFavorite = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create options menu to have the Favorite action appear
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        mFavoriteOption = menu.findItem(R.id.favorite_option);
        mUnfavoriteOption = menu.findItem(R.id.unfavorite_option);

        if (isAlreadyFavorite) {
            showUnfavorite();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorite_option) {
            // Save the current movie in the user's favorite's database
            saveToDatabase();
            showUnfavorite();
            return true;
        }

        if (id == R.id.unfavorite_option) {
            // Delete the current movie from the favorites database
            deleteFromDatabase();
            showFavorite();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveToDatabase() {
        // Create ContentValues to store relevant info for database
        ContentValues movieValues = new ContentValues();

        // Store the relevant information (title and moviedatabase id) in the content values
        movieValues.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID,
                mDetailMovie.getMovieDatabaseID());
        movieValues.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE,
                mDetailMovie.getMovieTitle());
        movieValues.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_LINK,
                mDetailMovie.getPosterLink());

        getContentResolver().insert(FavoritesContract.FavoritesEntry.CONTENT_URI, movieValues);
    }

    private void deleteFromDatabase() {
        // Delete the current movie from the favorites database
        Uri uri = FavoritesContract.FavoritesEntry.CONTENT_URI;
        String selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID + "=?";
        String[] selectionArgs = new String[]{mDetailMovie.getMovieDatabaseID()};

        getContentResolver().delete(uri, selection, selectionArgs);
    }

    private void showFavorite() {
        mFavoriteOption.setVisible(true);
        mUnfavoriteOption.setVisible(false);
    }

    private void showUnfavorite() {
        mUnfavoriteOption.setVisible(true);
        mFavoriteOption.setVisible(false);
    }

    @Override
    public android.support.v4.content.Loader<DetailMovie> onCreateLoader(int id, Bundle args) {
        return new DetailMovieLoader(this, mMovieDatabaseId);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<DetailMovie> loader, DetailMovie data) {
        // Set the data to the mDetailMenu variable
        mDetailMovie = data;

        // Set the title
        setTitle(data.getMovieTitle());

        // Set the movie data to the views in the DetailActivity
        Picasso.with(this)
                .load(BASE_URL + data.getBackdropLink())
                .into(mDetailImageView);
        mDetailRatingBar.setRating(data.getRating());
        mDetailReleaseDateView.setText(data.getReleaseDate());
        mDetailSynopsisView.setText(data.getSynopsis());

        // Get the number of trailers and the links
        String[] trailerLinks = data.getTrailerLinks();
        int numTrailers = trailerLinks.length;

        // If there are no trailers, skip the following steps and leave the No Trailers Found
        // textview visible.
        if (numTrailers > 0) {
            // Set the No Trailers Found view to GONE
            mNoTrailersFound.setVisibility(View.GONE);
            // Create a list of resource ids referencing the layouts and textviews
            int[] trailerViewResourceIds = new int[]{
                    R.id.detail_trailer_number_one,
                    R.id.trailer_index_text_view_one,
                    R.id.detail_trailer_number_two,
                    R.id.trailer_index_text_view_two,
                    R.id.detail_trailer_number_three,
                    R.id.trailer_index_text_view_three};

            // Populate the views with the trailer information. Only up to a maximum of 3 trailers.
            for (int i = 0; i < 3 && i < numTrailers; i++) {
                // Get references to the views
                View view = findViewById(trailerViewResourceIds[2 * i]);
                TextView textView = findViewById(trailerViewResourceIds[2 * i + 1]);

                // Get the trailer count text
                String trailerCount = getString(R.string.watch_trailer) + (i + 1);

                // Set the view to visible
                view.setVisibility(View.VISIBLE);

                // Set the trailer count text to the textview
                textView.setText(trailerCount);

                // Set the Youtube link information to the view in a tag
                view.setTag(trailerLinks[i]);

                // Set on click listener to the textview in order to send intent
                // to take user to watch the trailer
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openYoutubeIntent(view);
                    }
                });
            }
        }

        // Get the numbers of reviews
        int numReviews = data.getReviews().length;

        // If there are no reviews, skip the following steps and leave the No Reviews Found
        // textview visible
        if (numReviews > 0) {
            // Get the Array of Strings for the review content and authors
            String[] reviews = data.getReviews();
            String[] authors = data.getReviewAuthors();
            // Set the No Reviews Found view to GONE
            mNoReviewsFound.setVisibility(View.GONE);
            // Create a list of resource ids referencing the layouts and textviews
            int[] reviewsViewResourceIds = new int[]{
                    R.id.detail_review_one,
                    R.id.review_content_text_view_one,
                    R.id.review_author_text_view_one,
                    R.id.detail_review_two,
                    R.id.review_content_text_view_two,
                    R.id.review_author_text_view_two,
                    R.id.detail_review_three,
                    R.id.review_content_text_view_three,
                    R.id.review_author_text_view_three};

            // Populate the views with the review information. Only up to a maximum of 3 reviews.
            for (int i = 0; i < 3 && i < numReviews; i++) {
                // Get references to the views
                View view = findViewById(reviewsViewResourceIds[3 * i]);
                TextView contentTextView = findViewById(reviewsViewResourceIds[3 * i + 1]);
                TextView authorTextView = findViewById(reviewsViewResourceIds[3 * i + 2]);

                // Set the view to visible
                view.setVisibility(View.VISIBLE);

                // Set the review content to the textview
                contentTextView.setText(reviews[i]);
                authorTextView.setText(authors[i]);
            }

            // Check to see if the current movie is already included in the user's favorites
            checkFavoriteStatus();

            // Rebuild the optionsMenu to makes sure the correct Favorite/Unfavorite action is showing
            invalidateOptionsMenu();
        }
    }

    private void openYoutubeIntent(View view) {
        String youtubePath = (String) view.getTag();
        Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_BASE +
                youtubePath));
        Intent youtubeWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_WEB_BASE +
                youtubePath));
        try {
            startActivity(youtubeAppIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(youtubeWebIntent);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<DetailMovie> loader) {
    }

    @Override
    protected void onStop() {
        if (mCursor != null) {
            mCursor.close();
        }
        super.onStop();
    }
}
