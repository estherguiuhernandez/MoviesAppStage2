package com.example.moviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.database.FavoritesDatabase;
import com.example.moviesapp.database.MovieFavorites;
import com.example.moviesapp.utilities.MoviesJsonUtils;
import com.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GridRecyclerViewAdapter.OnMoviesListener {
    private static final String TAG = "MainActivity";
    private static final int NUM_GRID_COLUMS = 2;
    public static final String INSTANCE_SORT_BY_PARAMETER = "Sort Parameter";

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private String mSortbyParameter = "popular";

    private FavoritesViewModel favoritesViewModel;

    // Member variable for the Database
    private FavoritesDatabase mFavoritesDb;

    @Override
    protected void onSaveInstanceState(Bundle savedIstanceState) {
        super.onSaveInstanceState(savedIstanceState);
        savedIstanceState.putString(INSTANCE_SORT_BY_PARAMETER, mSortbyParameter);

    }


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
        mFavoritesDb = FavoritesDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SORT_BY_PARAMETER)) {
            mSortbyParameter = savedInstanceState.getString(INSTANCE_SORT_BY_PARAMETER);
        }


        if (mSortbyParameter != getString(R.string.favorites)) {
            loadMovieData();
        } else {
            loadMovieDataFromDatabase();
        }

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), NUM_GRID_COLUMS, RecyclerView.VERTICAL, false);
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
            mSortbyParameter = "popular";
            loadMovieData();
            return true;

        } else if (id == R.id.highest_rated) {
            mSortbyParameter = "top_rated";
            loadMovieData();
            return true;

        } else if (id == R.id.favorites) {
            mSortbyParameter = "favorites";
            loadMovieDataFromDatabase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovieDataFromDatabase() {
        favoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        favoritesViewModel.getAllFavoriteMovies().observe(this, new Observer<List<MovieFavorites>>() {
            @Override
            public void onChanged(List<MovieFavorites> movieFavorites) {
                // Create an empty ArrayList that we can start adding news to
                ArrayList<Movie> movies = new ArrayList<>();

                for (int i = 0; i < movieFavorites.size(); i++) {
                    MovieFavorites oneMovie = movieFavorites.get(i);
                    String title = oneMovie.getTitle();
                    String posterUrl = oneMovie.getPosterUrl();
                    String votes = "";
                    String synopsis = "";
                    String releaseDate = "";
                    int id = oneMovie.getMovieId();
                    movies.add(new Movie(id, title, synopsis, votes, releaseDate, posterUrl));
                }
                //update RecyclerView
                initRecyclerView(movies);
                Toast.makeText(MainActivity.this, "onChanged", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MoviesDbQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>> {

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

