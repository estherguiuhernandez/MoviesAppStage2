package com.example.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    /**
     * first part of path to retrieve image poster of movie
     */
    private final String mUrlPath = "http://image.tmdb.org/t/p/";
    /**
     * size of image poster of movie to include on path
     */
    private final String mImageSize = "w185/";
    /**
     * title of the movie
     */
    private String mTitle;
    /**
     * synopsis of the movie
     */
    private String mSynopsis;
    /**
     * vote of the movie
     */
    private String mVote;
    /**
     * /**
     * Release Date of the new
     */
    private String mDate;
    /**
     * /**
     * URL of the image where poster is
     */
    private String mPosterUrl;

    /**
     * /**
     * ID of the movie
     */
    private int mID;



    /**
     *
     */
    /**
     * Create a new movie object
     *
     * @param title     title of the movie
     * @param synopsis  synopsis of the movie
     * @param vote      votes of the movie
     * @param date      date release of the movie
     * @param posterUrl url of poster the movie
     * @param id integer representing id of the movie
     */
    public Movie(int id, String title, String synopsis, String vote, String date, String posterUrl) {
        mID = id;
        mTitle = title;
        mSynopsis = synopsis;
        mVote = vote;
        mDate = date;
        mPosterUrl = posterUrl;

    }

    /**
     * Retrieving Movie data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Movie(Parcel in) {
        mID = in.readInt();
        mTitle = in.readString();
        mSynopsis = in.readString();
        mVote = in.readString();
        mDate = in.readString();
        mPosterUrl = in.readString();

    }

    /**
     * Get the title of the movie
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * returns the Synopsis of the movie
     */
    public String getmSynopsis() {
        return mSynopsis;
    }

    /**
     * returns the year of release of movie
     */
    public String getmYear() {
        return mDate.split("-")[0];
    }

    /**
     * returns the date release of movie
     */
    public String getmDate() {
        return mDate;
    }

    /**
     * returns votes of  the movie
     */
    public String getmVote() {
        return mVote;
    }

    /**
     * returns the url of the poster of the movie
     */
    public String getmPosterUrl() {
        return mPosterUrl;
    }

    /**
     * returns the full url of the poster of the movie
     */
    public String getmFullPosterUrl() {

        if (mUrlPath == "null") {
            return null;
        } else {
            return mUrlPath + mImageSize + mPosterUrl;
        }
    }


    /**
     * Get the title of the movie
     */
    public int getmID() {
        return mID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    // Storing the Movie data to Parcel object
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mID);
        dest.writeString(mTitle);
        dest.writeString(mSynopsis);
        dest.writeString(mVote);
        dest.writeString(mDate);
        dest.writeString(mPosterUrl);

    }
}