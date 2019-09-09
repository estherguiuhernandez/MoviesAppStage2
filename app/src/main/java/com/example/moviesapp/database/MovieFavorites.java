package com.example.moviesapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites_table")
public class MovieFavorites {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private int movieId;

    public MovieFavorites(String title, int movieId) {
        this.title = title;
        this.movieId = movieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getMovieId() {
        return movieId;
    }
}
