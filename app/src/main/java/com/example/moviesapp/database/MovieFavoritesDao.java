package com.example.moviesapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieFavoritesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MovieFavorites movie);

    @Query("DELETE FROM favorites_table WHERE movieId =:mid")
    void delete(int mid);

    @Query("SELECT * FROM favorites_table")
    LiveData<List<MovieFavorites>> getAllMovies();

    @Query("SELECT * FROM favorites_table WHERE movieId = :mid")
    MovieFavorites loadMovieById(int mid);

}
