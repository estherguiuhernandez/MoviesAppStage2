package com.example.moviesapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.moviesapp.database.FavoritesRepository;
import com.example.moviesapp.database.MovieFavorites;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    private FavoritesRepository favoritesRepository;
    private LiveData<List<MovieFavorites>> allMovieFavorites;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        favoritesRepository = new FavoritesRepository(application);
        allMovieFavorites = favoritesRepository.getAllFavoriteMovies();
    }

    public void insert(MovieFavorites movieFavorites) {
        favoritesRepository.insert(movieFavorites);
    }

    public void delete(int movieID) {
        favoritesRepository.delete(movieID);

    }

    public LiveData<List<MovieFavorites>> getAllFavoriteMovies() {
        return allMovieFavorites;
    }
}
