package com.example.moviesapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.utilities.MoviesJsonUtils;
import com.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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
    final static String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
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
        mMovieVotes.setText(movie.getmVote() + getString(R.string.votes_overall));
        mMovieRelease.setText(movie.getmYear());
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

    public static void watchYoutubeVideo(Context context, String youtubeId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_URL + youtubeId));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public void addTrailers(int trailerNumber) {
        int textViewId = trailerNumber;
        //previousTextViewId = textViewId;


        ConstraintLayout layout = findViewById(R.id.cl_movie_details);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View trailerLayout = inflater.inflate(R.layout.layout_trailer, layout, false);
        trailerLayout.setId(textViewId);

        layout.addView(trailerLayout, lp);

        TextView trailer = trailerLayout.findViewById(R.id.tv_trailer_name);
        trailer.setText("Trailer " + trailerNumber);

        // Move the new view into place by applying constraints.
        ConstraintSet set = new ConstraintSet();
        // Get existing constraints. This will be the base for modification.
        set.clone(layout);
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, getResources().getDisplayMetrics());

        if (trailerNumber != 0) {
            int previousTextViewID = textViewId - 1;
            int currentid = trailerLayout.getId();
            set.connect(trailerLayout.getId(), ConstraintSet.TOP,
                    previousTextViewID, ConstraintSet.BOTTOM);
        } else {
            TextView trailerTitle = layout.findViewById(R.id.tv_trailer);
            set.connect(trailerLayout.getId(), ConstraintSet.TOP,
                    trailerTitle.getId(), ConstraintSet.BOTTOM, topMargin);
        }

        // Since views must be constrained vertically and horizontally, establish the horizontal
        // consrtaints such that the new view is centered.;
        //TODO Add horizontal constraints
        //set.centerHorizontally(middleView.getId(),ConstraintSet.PARENT_ID);
        // Finally, apply our good work to the layout.
        set.applyTo(layout);

    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, ArrayList<String>> {
        String extraSearchParameter = DBM_TRAILER_KEY;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }

            URL searchUrl = params[0];
            String url = searchUrl.toString();

            if (url.contains("reviews")) {
                extraSearchParameter = DBM_REVIEWS_KEY;
            }
            ArrayList<String> jsonMovieData;

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
        protected void onPostExecute(ArrayList<String> jsonMovieData) {
            if (jsonMovieData != null && !jsonMovieData.isEmpty()) {
                Log.d(TAG, "onPostExecute: " + jsonMovieData);
                // TODO: think about how to fill up the views
                for (int i = 0; i < 5; i++) {
                    addTrailers(i);
                }
                //if (extraSearchParameter != DBM_REVIEWS_KEY){
                //watchYoutubeVideo(MovieDetailsActivity.this, jsonMovieData.get(0));
                //}
                //fillupView(jsonMovieData, extraSearchParameter);
            } else {
                Log.d(TAG, "onPostExecute: ");
            }
        }
    }


}

