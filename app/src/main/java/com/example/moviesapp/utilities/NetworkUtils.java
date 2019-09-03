package com.example.moviesapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    // int to specify connection timeout to connect to open url
    final static int URL_CONNECTION_TIMEOUT_MS = 5000;

    // int to specify read time timeout to read from url once connection is opened
    final static int READ_TIME_TIMEOUT_MS = 10000;

    final static String BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    //final static String PARAM_QUERY = "q";

    /*
     * The sort field. Can be updated through configuration by user
     * Default: results are sorted by popularity in descendent order
     */
    //final static String PARAM_SORT = "sort_by";
    final static String sortBy = "popular";

    /*
     * The key field.
     * This parameter is deleted and needs for security when uploaded and needs to be added
     */
    final static String PARAM_KEY = "api_key";
    final static String myApiKey = "";

    /**
     * Builds the URL used to query moviesdb.
     *
     * @param sortByParameter parameter to sort by
     * @return The URL to use to query the moviesdb server.
     */
    public static URL buildUrl(String sortByParameter) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortByParameter)
                .appendQueryParameter(PARAM_KEY, myApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to query a reviews or trailer from the movie
     *
     * @param searchByParameter either movie of review
     * @param id id of the movie to be queried
     *
     * @return The URL to use to query the moviesdb server.
     */
    public static URL buildUrlMovies(String id, String searchByParameter) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(searchByParameter)
                .appendQueryParameter(PARAM_KEY, myApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // importance of setting timeouts: if an attempt to connect to URL failed the app would just sit there forever.
            // it won´t block UI because is inside an AsyncTask but task will never complete and we will waste resources
            // set the connection timeout to 5 seconds and the read timeout to 10 seconds
            urlConnection.setConnectTimeout(URL_CONNECTION_TIMEOUT_MS);
            urlConnection.setReadTimeout(READ_TIME_TIMEOUT_MS);

            InputStream in = urlConnection.getInputStream();

            // scanner is in charge of buffering the data
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}