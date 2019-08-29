package com.example.moviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.utilities.MoviesJsonUtils;
import com.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieRelease;
    private TextView mMovieVotes;
    private TextView mMovieSynopsis;
    private ImageView mMovieImage;
    private static final String TAG = "MovieDetailsActivity";
    final String MOVIE_TRAILER = "videos";
    final String MOVIE_REVIEWS = "reviews";
    final String DBM_TRAILER_KEY = "key";
    final String DBM_REVIEWS_KEY = "content";
    private int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        URL moviesSearchUrl;

        mMovieTitle = findViewById(R.id.tv_title_details);
        mMovieRelease = findViewById(R.id.tv_daterelease);
        mMovieVotes = findViewById(R.id.tv_votes);
        mMovieSynopsis = findViewById(R.id.tv_synopsis);
        mMovieImage = findViewById(R.id.iv_movie);

        Movie movie = getIntent().getParcelableExtra("movie");

        mID = movie.getmID();
        mMovieTitle.setText(movie.getmTitle());
        mMovieSynopsis.setText(movie.getmSynopsis());
        mMovieVotes.setText(movie.getmVote());
        mMovieRelease.setText(movie.getmDate());
        String movieUrl = movie.getmFullPosterUrl();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.picture_movie_brackground);

        Glide.with(this)
                .load(movie.getmFullPosterUrl())
                .apply(requestOptions)
                .into(mMovieImage);

        // run assynctask to retrieve the trailer of the movie
        moviesSearchUrl = NetworkUtils.buildUrlMovies(Integer.toString(mID), MOVIE_TRAILER);
        new MovieDbQueryTask().execute(moviesSearchUrl);

        // run assynctask to retrieve the reviews of the movie
        moviesSearchUrl = NetworkUtils.buildUrlMovies(Integer.toString(mID), MOVIE_REVIEWS);
        new MovieDbQueryTask().execute(moviesSearchUrl);

    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {
        String extraSearchParameter = DBM_TRAILER_KEY;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }

            URL searchUrl = params[0];
            String url = searchUrl.toString();


            if (url.contains("reviews")) {
                extraSearchParameter = DBM_REVIEWS_KEY;
            }
            String jsonMovieData;

            try {
                String moviesSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                jsonMovieData = MoviesJsonUtils.extractMovieExtraInfo(moviesSearchResults, extraSearchParameter);
                return jsonMovieData;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String jsonMovieData) {
            if (jsonMovieData != null && !jsonMovieData.isEmpty()) {
                Log.d(TAG, "onPostExecute: " + jsonMovieData);
                //fillupView(jsonMovieData, extraSearchParameter);
            } else {
                Log.d(TAG, "onPostExecute: ");
            }
        }
    }


}

