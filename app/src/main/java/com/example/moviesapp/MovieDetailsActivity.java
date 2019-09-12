package com.example.moviesapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.database.FavoritesRepository;
import com.example.moviesapp.database.MovieFavorites;
import com.example.moviesapp.utilities.MoviesJsonUtils;
import com.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mbuttonFavorites;


    private static final String TAG = "MovieDetailsActivity";
    final String MOVIE_TRAILER = "videos";
    final String MOVIE_REVIEWS = "reviews";
    final String DBM_TRAILER_KEY = "key";
    final String DBM_REVIEWS_KEY = "content";
    final static String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private int mLastLayoutID = 0;
    private Movie movie;

    private FavoritesRepository favoritesRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        favoritesRepository = new FavoritesRepository(getApplication());

        mbuttonFavorites = findViewById(R.id.bt_add_favorites);
        mbuttonFavorites.setOnClickListener(this);

        URL moviesSearchUrl;

        TextView movieTitle = findViewById(R.id.tv_title_details);
        TextView movieRelease = findViewById(R.id.tv_daterelease);
        TextView movieVotes = findViewById(R.id.tv_votes);
        TextView movieSynopsis = findViewById(R.id.tv_synopsis);
        ImageView movieImage = findViewById(R.id.iv_movie);
        final Button buttonFavorites = findViewById(R.id.bt_add_favorites);

        movie = getIntent().getParcelableExtra("movie");

        int mID = movie.getmID();
        movieTitle.setText(movie.getmTitle());
        movieSynopsis.setText(movie.getmSynopsis());
        movieVotes.setText(getString(R.string.votes_overall, movie.getmVote()));
        movieRelease.setText(movie.getmYear());

        // TODO: HOW TO RETRIEVE the movie from here, i can´t seem to find a solution to retrieve the value from the assynctask!

        OnAsyncTaskCompleted onAsyncTaskImplementation = new OnAsyncTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean doesMovieExist) {
                if (doesMovieExist) {
                    buttonFavorites.setText(getString(R.string.button_favorites_text_remove));
                } else {
                    // maybe this is not needed as is it the default value
                    buttonFavorites.setText(getString(R.string.button_favorites_add_text));
                }
            }
        };

        favoritesRepository.loadMoviebyId(mID, onAsyncTaskImplementation);



        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.picture_movie_brackground);

        Glide.with(this)
                .load(movie.getmFullPosterUrl())
                .apply(requestOptions)
                .into(movieImage);

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
        reviewTitleView.setText(getString(R.string.review_text, reviewLayoutNumber));

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
        trailerLayout.setOnClickListener(this);


        trailerLayout.setId(View.generateViewId());
        int trailerLayoutnewID = trailerLayout.getId();
        trailerLayout.setTag(trailerYoutubeID);

        constraintLayout.addView(trailerLayout, lp);

        TextView trailer = trailerLayout.findViewById(R.id.tv_trailer_name);

        trailer.setText(getString(R.string.trailer_text, trailerLayoutNumber));

        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, getResources().getDisplayMetrics());

        // Move the new view into place by applying constraints.
        ConstraintSet set = new ConstraintSet();
        // Get existing constraints. This will be the base for modification.
        set.clone(constraintLayout);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
            case R.id.bt_add_favorites:
                Button bFavorites = (Button) v;
                String buttonText = bFavorites.getText().toString();
                String search = "Add";
                if (buttonText.toLowerCase().indexOf(search.toLowerCase()) != -1) {
                    // if it contains contain mark means means we need add to database and set text to remove
                    addFavoriteToDb();
                    mbuttonFavorites.setText(getString(R.string.button_favorites_text_remove));
                } else {
                    removeFavoriteFromDb();
                    mbuttonFavorites.setText(getString(R.string.button_favorites_add_text));
                }

                break;
            default:
                // default case means is a youtube video, can´t get the id like int he other one, since it was stablished automatically
                String youtubeID = v.getTag().toString();
                if (youtubeID != null && !youtubeID.isEmpty()) {
                    watchYoutubeVideo(MovieDetailsActivity.this, youtubeID);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_playing_trailer, Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    // this method is called when the favorites button is clicked, and the status is "remove to favorites"
    // it then removes the current movie from favorites
    private void removeFavoriteFromDb() {
        int id = movie.getmID();
        favoritesRepository.delete(id);

    }

    // this method is called when the favorites button is clicked, and the status is "add to favorites"
    // it then adds the current movie to favorites
    private void addFavoriteToDb() {
        String title = movie.getmTitle();
        String posterUrl = movie.getmPosterUrl();
        int id = movie.getmID();
        final MovieFavorites movieFavorites = new MovieFavorites(title, id, posterUrl);
        favoritesRepository.insert(movieFavorites);

    }


    public interface OnAsyncTaskCompleted {
        void onTaskCompleted(boolean doesMovieExist);
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
                    int previousLayoutID = 0;
                    for (int i = 0; i < jsonMovieData.size(); i++) {
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

