package com.example.moviesapp.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

// repository adds abstractions
public class FavoritesRepository {

    LiveData<MovieFavorites> movieFavorites;
    private MovieFavoritesDao movieFavoritesDao;
    private LiveData<List<MovieFavorites>> allFavoriteMovies;


    public FavoritesRepository(Application application) {

        FavoritesDatabase favoritesDatabase = FavoritesDatabase.getInstance(application);
        movieFavoritesDao = favoritesDatabase.favoritesDao();
        allFavoriteMovies = movieFavoritesDao.getAllMovies();
    }

    public void insert(MovieFavorites movieFavorites) {
        new InsertMovieFavoriteAssyncTask(movieFavoritesDao).execute(movieFavorites);

    }

    public void delete(MovieFavorites movieFavorites) {
        new DeleteMovieFavoriteAssyncTask(movieFavoritesDao).execute(movieFavorites);
    }

    public LiveData<List<MovieFavorites>> getAllFavoriteMovies() {
        return allFavoriteMovies;
    }

    public void loadMoviebyId(int id) {
        new LoadMovieFavoriteByIdAssyncTask(movieFavoritesDao).execute(id);
    }


    // we need to perform database operations on background thread, need to do this manually
    private static class InsertMovieFavoriteAssyncTask extends AsyncTask<MovieFavorites, Void, Void> {

        private MovieFavoritesDao movieFavoritesDao;

        public InsertMovieFavoriteAssyncTask(MovieFavoritesDao movieFavoritesDao) {
            this.movieFavoritesDao = movieFavoritesDao;
        }

        @Override
        protected Void doInBackground(MovieFavorites... movieFavorites) {
            movieFavoritesDao.insert(movieFavorites[0]);
            return null;
        }
    }


    // we need to perform database operations on background thread, need to do this manually
    private static class DeleteMovieFavoriteAssyncTask extends AsyncTask<MovieFavorites, Void, Void> {

        private MovieFavoritesDao movieFavoritesDao;


        public DeleteMovieFavoriteAssyncTask(MovieFavoritesDao movieFavoritesDao) {
            this.movieFavoritesDao = movieFavoritesDao;
        }

        @Override
        protected Void doInBackground(MovieFavorites... movieFavorites) {
            movieFavoritesDao.delete(movieFavorites[0]);
            return null;
        }
    }


    // we need to perform database operations on background thread, need to do this manually
    private static class LoadMovieFavoriteByIdAssyncTask extends AsyncTask<Integer, Void, LiveData<MovieFavorites>> {

        private MovieFavoritesDao movieFavoritesDao;
        private LiveData<MovieFavorites> movieFavorites;

        public LoadMovieFavoriteByIdAssyncTask(MovieFavoritesDao movieFavoritesDao) {
            this.movieFavoritesDao = movieFavoritesDao;
        }

        @Override
        protected LiveData<MovieFavorites> doInBackground(Integer... movieId) {
            this.movieFavorites = movieFavoritesDao.loadMovieById(movieId[0]);
            return movieFavorites;
        }
    }

}
