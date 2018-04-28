package com.example.android.popularmovies;

import android.content.CursorLoader;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.data.FavoritesContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>{

    // Keys used for list order preference
    private static final String SHARED_PREFERENCE_FILE = "shared_preferences";
    private static final String SHARED_PREFERENCE_KEY = "order_preference";
    private static final String POPULAR_PATH_KEY = "Popular";
    private static final String TOP_RATED_PATH_KEY = "Top Rated";
    private static final String FAVORITES_KEY = "Favorites";
    private static final int SPINNER_POPULAR_INDEX = 0;
    private static final int SPINNER_TOP_RATED_INDEX = 1;
    private static final int SPINNER_FAVORITES_INDEX = 2;

    private static final int POP_TOP_RATED_LOADER_ID = 123;

    private static final String BUNDLE_RECYCLER_VIEW_LAYOUT = "mainactivity.recycler.layout";
    private static Parcelable mLayoutManagerSavedState;

    private Cursor mCursor;

    private boolean mIsConnected;

    private static final String LOG_TAG = "MainActivity.java";

    // Declare member variables
    @BindView(R.id.recycler_view_movies)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.empty_view)
    TextView mEmptyTextView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.reload_button)
    Button mReloadButton;
    private MovieAdapter mMovieAdapter;

    // Use mPreference to keep track of the User's preference. Default is "Popular"
    static String mPreference = TOP_RATED_PATH_KEY;

    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        // initialize mSharedPreferences; Set "Popular" as default
        mSharedPref = getSharedPreferences(SHARED_PREFERENCE_FILE, MODE_PRIVATE);
        mPreference = mSharedPref.getString(SHARED_PREFERENCE_KEY, POPULAR_PATH_KEY);

        // Change the columns for the gridlayout depending on whether the phone is in Portrait
        // mode or not.
        int spanCount;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 2;
        } else {
            spanCount = 4;
        }

        // Create a grid layout manager and set to recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount,
                GridLayoutManager.VERTICAL, false);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);

        // Initialize mMovieAdapter and set it to the recycler view
        mMovieAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check to see if there were any changes in the Database and if the current preference
        // is for favorite movies: if so, reload the favorite movies list, this time without the
        // deleted favorite moive
        if (data != null) {
            if (data.hasExtra(DetailActivity.EXTRA_DATABASE_CHANGE_NAME) &&
                    mPreference.equals(FAVORITES_KEY)) {
                mMovieAdapter.setMovieData(null);
                setFavoritesListToUI();
            }
        }
    }

    // Method for checking network connectivity
    private void checkNetworkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mIsConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showEmptyTextView() {
        mMoviesRecyclerView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);

        if (!mIsConnected) {
            mEmptyTextView.setText(R.string.no_internet_connection);
            mReloadButton.setVisibility(View.VISIBLE);
        } else if (mPreference.equals(FAVORITES_KEY)) {
            mEmptyTextView.setText(R.string.no_favorite_movies);
        } else {
            mEmptyTextView.setText(R.string.no_movies_loaded);
        }
    }

    public void tryReloading(View view) {
        if (mPreference.equals(getString(R.string.settings_order_by_favorites))) {
            hideEmptyTextView();
            mProgressBar.setVisibility(View.VISIBLE);
            setFavoritesListToUI();
        } else {
            // Restart Loader
            getSupportLoaderManager().restartLoader(POP_TOP_RATED_LOADER_ID, null,
                    MainActivity.this);
        }
    }

    private void hideEmptyTextView() {
        mReloadButton.setVisibility(View.GONE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
        mEmptyTextView.setVisibility(View.GONE);
    }

    // When a movie image is clicked, take the user to the movie's detail page
    @Override
    public void onClick(Movie movie) {
        // Send an intent to open the DetailActivity.class using the selected movie as a parameter
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID,
                movie.getMovieDatabaseID());

        startActivityForResult(intent, 123);
    }

    // Create an options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.settings_spinner);
        Spinner spinner = (Spinner) item.getActionView();

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getSupportActionBar().getThemedContext(), R.array.settings_order_by_labels,
                R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        switch (mPreference) {
            case TOP_RATED_PATH_KEY:
                spinner.setSelection(SPINNER_TOP_RATED_INDEX);
                break;
            case FAVORITES_KEY:
                spinner.setSelection(SPINNER_FAVORITES_INDEX);
                break;
            default:
                spinner.setSelection(SPINNER_POPULAR_INDEX);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the selected item
                String selectedItem = adapterView.getItemAtPosition(position).toString();

                if (selectedItem != null && !selectedItem.isEmpty()) {
                    mPreference = selectedItem;

                    // Set the new preference in Shared Preferences
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putString(SHARED_PREFERENCE_KEY, selectedItem);
                    editor.apply();

                    // Clear previous movie adapter
                    mMovieAdapter.setMovieData(null);

                    if (selectedItem.equals(getString(R.string.settings_order_by_favorites))) {
                        hideEmptyTextView();
                        mProgressBar.setVisibility(View.VISIBLE);
                        setFavoritesListToUI();
                    } else {
                        // Restart Loader
                        getSupportLoaderManager().restartLoader(POP_TOP_RATED_LOADER_ID, null,
                                MainActivity.this);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return true;
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        hideEmptyTextView();
        mProgressBar.setVisibility(View.VISIBLE);
        return new MovieLoader(this, mPreference);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        if (!mPreference.equals(FAVORITES_KEY)) {
            // First clear MovieAdapter
            mMovieAdapter.setMovieData(null);

            // Hide Progress Bar
            mProgressBar.setVisibility(View.GONE);

            // Check if Data has been retrieved properly
            if (data != null && data.size() > 0) {
                mMovieAdapter.setMovieData(data);
            } else {
                checkNetworkConnectivity();
                showEmptyTextView();
            }
        }

        mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        mMovieAdapter.setMovieData(null);
    }

    private void setFavoritesListToUI() {
        // Query the favorite movies from the database
        List<Movie> favoriteMovies = getListOfFavoriteMovies();

        if (favoriteMovies == null || favoriteMovies.size() == 0) {
            checkNetworkConnectivity();
            mProgressBar.setVisibility(View.GONE);
            showEmptyTextView();
        } else {
            // Set the loading indicator to gone
            mProgressBar.setVisibility(View.GONE);

            // Use the information from the database to populate the UI
            mMovieAdapter.setMovieData(favoriteMovies);
        }
    }

    private List<Movie> getListOfFavoriteMovies() {
        // Check if device is connected to network first because we do not want
        // to load the list of favorite movies and then have the app crash when the user
        // tries to select a movie but cannot go on to the detail activity
        checkNetworkConnectivity();
        if (!mIsConnected) {
            showEmptyTextView();
        }

        mCursor = new CursorLoader( this,
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        ).loadInBackground();

        // return null if cursor is null
        if (mCursor == null) {
            return null;
        }

        // Declare and initialize List of favorite movies
        List<Movie> favoriteMovies = new ArrayList<>();

        // Iterate through cursor to get the information to make Movie objects to put into the
        // the list
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String movieDatabaseId = mCursor.getString(mCursor.getColumnIndex(
                    FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID));
            String posterLink = mCursor.getString(mCursor.getColumnIndex(
                    FavoritesContract.FavoritesEntry.COLUMN_POSTER_LINK));

            Movie currentMovie = new Movie(movieDatabaseId, posterLink);
            favoriteMovies.add(currentMovie);
        }

        return favoriteMovies;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_LAYOUT, mMoviesRecyclerView.getLayoutManager().
                onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_VIEW_LAYOUT);
            if (mLayoutManagerSavedState != null) {
                mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
            }

        }
    }
}
