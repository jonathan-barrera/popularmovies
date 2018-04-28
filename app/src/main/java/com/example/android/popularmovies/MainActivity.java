package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        LoaderManager.LoaderCallbacks<List<Movie>> {

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
    private static Bundle mRecyclerViewState;

    private static final String LOG_TAG = "MainActivity.java";

    // Declare member variables
    @BindView(R.id.recycler_view_movies)
    RecyclerView mMoviesRecyclerView;
    @BindView(R.id.empty_view)
    TextView mEmptyTextView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private MovieAdapter mMovieAdapter;

    // Use mPreference to keep track of the User's preference. Default is "Popular"
    static String mPreference = TOP_RATED_PATH_KEY;

    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "oncreate called");

        // Bind views
        ButterKnife.bind(this);

        // initialize mSharedPreferences; Set "Popular" as default
        mSharedPref = getSharedPreferences(SHARED_PREFERENCE_FILE, MODE_PRIVATE);
        mPreference = mSharedPref.getString(SHARED_PREFERENCE_KEY, POPULAR_PATH_KEY);

        // Create a grid layout manager and set to recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);

        // Initialize mMovieAdapter and set it to the recycler view
        mMovieAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on resume called");
        super.onResume();

        if (mRecyclerViewState != null) {
            Parcelable listState = mRecyclerViewState.getParcelable(BUNDLE_RECYCLER_VIEW_LAYOUT);
            mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    // Method for checking network connectivity
    private void checkNetworkConnectivity() {
        Log.d(LOG_TAG, "check newtork connectivity called");
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Hide RecyclerView and set the empty text view
        mMoviesRecyclerView.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);

        if (!isConnected) {
            mEmptyTextView.setText(R.string.no_internet_connection);
        } else {
            mEmptyTextView.setText(R.string.no_movies_loaded);
        }
    }

    // When a movie image is clicked, take the user to the movie's detail page
    @Override
    public void onClick(Movie movie) {
        Log.d(LOG_TAG, "on click called");
        // Send an intent to open the DetailActivity.class using the selected movie as a parameter
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID,
                movie.getMovieDatabaseID());

        startActivity(intent);
    }

    // Create an options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "on create options menu called");
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
                Log.d(LOG_TAG, "on item selected called");
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
                Log.d(LOG_TAG, "on nothing selected called");
            }
        });

        return true;
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "on create loader called");
        return new MovieLoader(this, mPreference);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        Log.d(LOG_TAG, "on load finished called");
        if (!mPreference.equals(FAVORITES_KEY)) {
            // First clear MovieAdapter
            mMovieAdapter.setMovieData(null);

            // Hide Progress Bar
            mProgressBar.setVisibility(View.GONE);

            // Check if Data has been retrieved properly
            if (data != null && data.size() > 0) {
                mMovieAdapter.setMovieData(data);
                mMoviesRecyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerViewState);
            } else {
                checkNetworkConnectivity();
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        Log.d(LOG_TAG, "on loader reset called");
        mMovieAdapter.setMovieData(null);
    }

    private void setFavoritesListToUI() {
        Log.d(LOG_TAG, "set favorites list to ui called");
        // Query the favorite movies from the database
        List<Movie> favoriteMovies = getListOfFavoriteMovies();

        // Set the loading indicator to gone
        mProgressBar.setVisibility(View.GONE);

        // Use the information from the database to populate the UI
        mMovieAdapter.setMovieData(favoriteMovies);
    }

    private List<Movie> getListOfFavoriteMovies() {
        Log.d(LOG_TAG, "getListOfFavoriteMovies() called");
        Cursor cursor = getContentResolver().query(
                FavoritesContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // return null if cursor is null
        if (cursor == null) {
            return null;
        }

        // Declare and initialize List of favorite movies
        List<Movie> favoriteMovies = new ArrayList<>();

        // Iterate through cursor to get the information to make Movie objects to put into the
        // the list
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String movieDatabaseId = cursor.getString(cursor.getColumnIndex(
                    FavoritesContract.FavoritesEntry.COLUMN_MOVIE_DATABASE_ID));
            String posterLink = cursor.getString(cursor.getColumnIndex(
                    FavoritesContract.FavoritesEntry.COLUMN_POSTER_LINK));

            Movie currentMovie = new Movie(movieDatabaseId, posterLink);
            favoriteMovies.add(currentMovie);
        }

        // Close cursor.
        cursor.close();

        return favoriteMovies;
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "onstop called");
        super.onStop();

        mRecyclerViewState = new Bundle();
        Parcelable recyclerState = mMoviesRecyclerView.getLayoutManager().onSaveInstanceState();
        mRecyclerViewState.putParcelable(BUNDLE_RECYCLER_VIEW_LAYOUT, recyclerState);
    }
}
