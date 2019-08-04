package com.example.moviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moviesapp.utilities.MoviesJsonUtils;
import com.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.OnMoviesListener {
    private static final String TAG = "MainActivity";
    private static final int NUM_GRID_COLUMS = 3;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private String mSortbyParameter = "popularity.desc";

    private ArrayList<String> mImageUrl = new ArrayList<>();
    private ArrayList<String> mMovieTittle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mRecyclerView = findViewById(R.id.rv_movies);
        loadMovieData();
    }

    /**
     * This method will get the user's preferred search orders, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();
        //String location = SunshinePreferences.getPreferredWeatherLocation(this);
        makeMoviesDbSearchQuery();
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    /**
     * This method initializes the recycler view and its adapter
     */
    private void initRecyclerView(ArrayList<Movie> jsonMovieData) {
        Log.d(TAG, "initRecyclerView method");
        // the scond this refers to the interface
        GridRecyclerViewAdapter gridReyclerViewAdapter =
                new GridRecyclerViewAdapter(MainActivity.this, this, jsonMovieData);
        // set a GridLayoutManager with NUM_GRID_COLUMS number of columns , horizontal gravity and false
        // value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), NUM_GRID_COLUMS, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerVie
        mRecyclerView.setAdapter(gridReyclerViewAdapter);
    }


    @Override
    public void onMovieCLick(Movie currentMovie) {
        Log.d(TAG, "onMovieCLick: Clicked");
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", currentMovie);
        startActivity(intent);
    }

    /**
     * This constructs the
     * URL (using {@link NetworkUtils}) based on user preferences for the movies displays
     * and  fires off an AsyncTask to perform the GET request using
     * our {@link MoviesDbQueryTask}
     */
    private void makeMoviesDbSearchQuery() {
        //TODO modify URL to be based on user preferences depending on search menu
        URL moviesSearchUrl = NetworkUtils.buildUrl(mSortbyParameter);
        new MoviesDbQueryTask().execute(moviesSearchUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.most_popular) {
            mSortbyParameter = "popularity.desc";
            loadMovieData();
            return true;

        } else if (id == R.id.highest_rated) {
            mSortbyParameter = "vote_average.desc";
            loadMovieData();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public class MoviesDbQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

        // TODO Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }
            URL searchUrl = params[0];
            ArrayList<Movie> jsonMovieData;

            try {
                String moviesSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                jsonMovieData = MoviesJsonUtils.extractMovies(moviesSearchResults);
                return jsonMovieData;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> jsonMovieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (jsonMovieData != null && !jsonMovieData.isEmpty()) {
                showMovieDataView();
                initRecyclerView(jsonMovieData);
            } else {
                showErrorMessage();
            }
        }
    }

}

