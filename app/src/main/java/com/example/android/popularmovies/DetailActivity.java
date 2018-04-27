package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        loaderManager.initLoader(2, null, this);
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
        // Set the title
        setTitle(data.getMovieTitle());

        // Set the movie data to the views in the DetailActivity
        Picasso.with(this)
                .load(BASE_URL + data.getBackdropLink())
                .into(mDetailImageView);
        mDetailRatingBar.setRating(data.getRating());
        mDetailReleaseDateView.setText(data.getReleaseDate());
        mDetailSynopsisView.setText(data.getSynopsis());

        mDetailMovie = data;

        // Check to see if the current movie is already included in the user's favorites
        checkFavoriteStatus();

        // Rebuild the optionsMenu to makes sure the correct Favorite/Unfavorite action is showing
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<DetailMovie> loader) {
    }

    @Override
    protected void onStop() {
        mCursor.close();
        super.onStop();
    }
}
