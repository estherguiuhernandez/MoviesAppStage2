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

    final static String BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";

    //final static String PARAM_QUERY = "q";

    /*
     * The sort field. Can be updated through configuration by user
     * Default: results are sorted by popularity in descendent order
     */
    final static String PARAM_SORT = "sort_by";
    final static String sortBy = "popularity.desc";

    /*
     * The key field.
     * This parameter is deleted and needs for security when uploaded and needs to be added
     */
    final static String PARAM_KEY = "api_key";
    final static String myApiKey = "";

    /**
     * Builds the URL used to query moviesdb.
     *
     * @param sortbyParameter The keyword that will be queried for.
     * @return The URL to use to query the moviesdb server.
     */
    public static URL buildUrl(String sortbyParameter) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_SORT, sortbyParameter)
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