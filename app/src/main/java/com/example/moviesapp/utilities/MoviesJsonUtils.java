package com.example.moviesapp.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.moviesapp.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoviesJsonUtils {

    private static final String TAG = "MoviesJsonUtils";

    /**
     * Return a list of {@link Movie} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Movie> extractMovies(String moviesJSON) {

        /* JASON parameters to query */
        final String DBM_TITLE = "title";
        final String DBM_POSTERPATH = "poster_path";
        final String DBM_VOTE = "vote_average";
        final String DBM_SYNOPSIS = "overview";
        final String DBM_RELEASEDATE = "release_date";
        final String DBM_ID = "id";
        final String DBM_DURATION = "duration";


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        ArrayList<Movie> movies = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject jsonObj = new JSONObject(moviesJSON);

            // Getting JSON Array node
            JSONArray allMovies = jsonObj.getJSONArray("results");

            for (int i = 0; i < allMovies.length(); i++) {
                JSONObject oneMovie = allMovies.getJSONObject(i);

                String title = oneMovie.getString(DBM_TITLE);
                String posterUrl = oneMovie.getString(DBM_POSTERPATH);
                String votes = oneMovie.getString(DBM_VOTE);
                String synopsis = oneMovie.getString(DBM_SYNOPSIS);
                String releaseDate = oneMovie.getString(DBM_RELEASEDATE);
                int id = oneMovie.getInt(DBM_ID);
                //TODO: eliminate hardcoded string once i know if duration can be retrieved
                String duration = "120min";
                movies.add(new Movie(id, title, synopsis, votes, releaseDate, posterUrl, duration));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(TAG, "extractMovies: ", e);
        }
        // Return the list of movies
        return movies;
    }


    /**
     * Returns a String object either containing the review or the trailer address
     *
     * @param movieJSON the jason data to be parsed
     */
    public static ArrayList<String> extractMovieExtraInfo(String movieJSON, String extraParameters) {

        /* JASON parameters to query */


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        ArrayList<String> extraInfo = new ArrayList<>();

        try {

            JSONObject jsonObj = new JSONObject(movieJSON);
            // Getting JSON Array node
            JSONArray trailerMovies = jsonObj.getJSONArray("results");

            for (int i = 0; i < trailerMovies.length(); i++) {
                JSONObject trailerMovie = trailerMovies.getJSONObject(i);
                extraInfo.add(trailerMovie.getString(extraParameters));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(TAG, "extractMovies: ", e);
        }
        // Return the list of movies
        return extraInfo;
    }
}
