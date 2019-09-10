package com.example.moviesapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites_table")
public class MovieFavorites {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private int movieId;

    private String posterUrl;

    public MovieFavorites(String title, int movieId, String posterUrl) {
        this.title = title;
        this.movieId = movieId;
        this.posterUrl = posterUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public int getMovieId() {
        return this.movieId;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }
}
