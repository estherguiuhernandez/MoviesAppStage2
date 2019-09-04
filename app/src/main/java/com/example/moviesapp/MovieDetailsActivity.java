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
import android.widget.Toast;

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
    private int mLastLayoutID = 0;

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

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String youtubeID = v.getTag().toString();
            if (youtubeID != null && !youtubeID.isEmpty()) {
                watchYoutubeVideo(MovieDetailsActivity.this, youtubeID);
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_playing_trailer, Toast.LENGTH_LONG).show();
            }

        }
    };

    public int addreviews(int reviewNumber, int previousReviewID, String reviewText) {

        int reviewLayoutNumber = reviewNumber + 1;

        ConstraintLayout constraintLayout = findViewById(R.id.cl_movie_details);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View reviewsLayout = inflater.inflate(R.layout.layout_reviews, constraintLayout, false);

        reviewsLayout.setId(View.generateViewId());
        int reviewsLayoutnewID = reviewsLayout.getId();


        constraintLayout.addView(reviewsLayout, lp);

        TextView reviewTitleView = reviewsLayout.findViewById(R.id.tv_review_title);
        reviewTitleView.setText("Review " + reviewLayoutNumber);

        TextView reviewTextView = reviewsLayout.findViewById(R.id.tv_review_text);
        reviewTextView.setText(reviewText);


        // Move the new view into place by applying constraints.
        ConstraintSet set = new ConstraintSet();
        // Get existing constraints. This will be the base for modification.
        set.clone(constraintLayout);
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, getResources().getDisplayMetrics());

        if (reviewNumber != 0) {
            set.connect(reviewsLayoutnewID, ConstraintSet.TOP,
                    previousReviewID, ConstraintSet.BOTTOM);
        } else {
            if (mLastLayoutID != 0) {
                set.connect(reviewsLayoutnewID, ConstraintSet.TOP,
                        mLastLayoutID, ConstraintSet.BOTTOM, topMargin);
            } else {
                TextView trailerTitle = constraintLayout.findViewById(R.id.tv_trailer);
                set.connect(reviewsLayoutnewID, ConstraintSet.TOP,
                        trailerTitle.getId(), ConstraintSet.BOTTOM, topMargin);
            }

        }

        // Since views must be constrained vertically and horizontally, establish the horizontal
        // constraint such that the new view is attached to the left of the parent
        set.connect(reviewsLayoutnewID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        // Since views must be constrained vertically and horizontally, establish the horizontal
        // constraint such that the new view is attached to the left of the parent
        set.connect(reviewsLayoutnewID, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        set.constrainDefaultWidth(reviewsLayoutnewID, ConstraintSet.MATCH_CONSTRAINT_SPREAD);

        set.applyTo(constraintLayout);

        return reviewsLayoutnewID;

    }

    /**
     * Adds the required number of trailers dinamically to the activity_movie_details layout
     * returns the id of the just added layout
     * @param trailerNumber number of the trailer to be added
     * @param previousTrailerLayoutId id of the previous trailer to be used for constrained purposes
     */

    public int addTrailers(int trailerNumber, int previousTrailerLayoutId, String trailerYoutubeID) {
        int trailerLayoutNumber = trailerNumber + 1;

        ConstraintLayout constraintLayout = findViewById(R.id.cl_movie_details);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View trailerLayout = inflater.inflate(R.layout.layout_trailer, constraintLayout, false);
        trailerLayout.setOnClickListener(clickListener);

        trailerLayout.setId(View.generateViewId());
        int trailerLayoutnewID = trailerLayout.getId();
        trailerLayout.setTag(trailerYoutubeID);

        constraintLayout.addView(trailerLayout, lp);

        TextView trailer = trailerLayout.findViewById(R.id.tv_trailer_name);
        trailer.setText("Trailer " + trailerLayoutNumber);

        // Move the new view into place by applying constraints.
        ConstraintSet set = new ConstraintSet();
        // Get existing constraints. This will be the base for modification.
        set.clone(constraintLayout);
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, getResources().getDisplayMetrics());

        if (trailerNumber != 0) {
            set.connect(trailerLayoutnewID, ConstraintSet.TOP,
                    previousTrailerLayoutId, ConstraintSet.BOTTOM);
        } else {
            TextView trailerTitle = constraintLayout.findViewById(R.id.tv_trailer);
            set.connect(trailerLayoutnewID, ConstraintSet.TOP,
                    trailerTitle.getId(), ConstraintSet.BOTTOM, topMargin);
        }

        // Since views must be constrained vertically and horizontally, establish the horizontal
        // consrtaints such that the new view is attached to the left of the parent
        set.connect(trailerLayoutnewID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.constrainDefaultWidth(trailerLayoutnewID, ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.applyTo(constraintLayout);

        return trailerLayoutnewID;

    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, ArrayList<String>> {
        String extraSearchParameter = DBM_TRAILER_KEY;
        boolean isTrailer = true;

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
                isTrailer = false;

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
            int currentLayoutID = 0;
            if (jsonMovieData != null && !jsonMovieData.isEmpty()) {
                Log.d(TAG, "onPostExecute: " + jsonMovieData);
                if (isTrailer) {
                    int previousLayoutID = 0;
                    for (int i = 0; i < jsonMovieData.size(); i++) {
                        currentLayoutID = addTrailers(i, previousLayoutID, jsonMovieData.get(i));
                        previousLayoutID = currentLayoutID;
                    }
                } else {
                    for (int i = 0; i < jsonMovieData.size(); i++) {
                        int previousLayoutID = 0;
                        currentLayoutID = addreviews(i, previousLayoutID, jsonMovieData.get(i));
                        previousLayoutID = currentLayoutID;
                    }
                }
                mLastLayoutID = currentLayoutID;
            } else {
                Log.d(TAG, "onPostExecute: ");
            }
        }
    }

}

