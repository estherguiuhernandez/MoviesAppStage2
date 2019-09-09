package com.example.moviesapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieFavoritesDao {

    @Insert
    void insert(MovieFavorites movie);

    @Delete
    void delete(MovieFavorites movie);

    @Query("SELECT * FROM favorites_table")
    LiveData<List<MovieFavorites>> getAllMovies();

    @Query("SELECT * FROM favorites_table WHERE id = :id")
    LiveData<MovieFavorites> loadMovieById(int id);

}
