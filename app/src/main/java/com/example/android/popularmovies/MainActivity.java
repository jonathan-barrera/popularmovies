package com.example.android.popularmovies;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;

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
    private static final int SPINNER_POPULAR_INDEX = 0;
    private static final int SPINNGER_TOP_RATED_INDEX = 1;

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

        // Get a reference to the LoaderManager and initialize it
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(1, null, this);
    }

    // Method for checking network connectivity. If there is a problem with the connect,
    // set the empty text view to "No Internet Connection". If no problem with connect, set
    // to "No Movies Downloaded"
    private void checkNetworkConnectivity() {
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
        // Send an intent to open the DetailActivity.class using the selected movie as a parameter
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(getString(R.string.movie_key), movie);

        startActivity(intent);
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

        if (mPreference.equals(POPULAR_PATH_KEY)) {
            spinner.setSelection(SPINNER_POPULAR_INDEX);
        } else if (mPreference.equals(TOP_RATED_PATH_KEY)) {
            spinner.setSelection(SPINNGER_TOP_RATED_INDEX);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the selected item
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                if (selectedItem != null && !selectedItem.isEmpty()) {
                    mPreference = selectedItem;

                    // Set the new preference in Shared Preferences
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putString(SHARED_PREFERENCE_KEY, selectedItem);
                    editor.apply();
                } else {
                    return;
                }

                mMovieAdapter.setMovieData(null);
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return true;
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, mPreference);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        // First clear MovieAdapter
        mMovieAdapter.setMovieData(null);

        // Hide Progress Bar
        mProgressBar.setVisibility(View.GONE);

        // Check if Data has been retrieved properly
        if (data != null && data.size() > 0) {
            mMovieAdapter.setMovieData(data);
        } else {
            checkNetworkConnectivity();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        mMovieAdapter.setMovieData(null);
    }
}
