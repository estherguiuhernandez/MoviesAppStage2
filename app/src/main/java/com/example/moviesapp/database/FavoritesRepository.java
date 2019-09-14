package com.example.moviesapp.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.moviesapp.MovieDetailsActivity;

import java.util.List;

// repository adds abstractions
public class FavoritesRepository {

    LiveData<MovieFavorites> movieFavorites;
    private MovieFavoritesDao movieFavoritesDao;
    private LiveData<List<MovieFavorites>> allFavoriteMovies;
    //private MovieDetailsActivity.OnAsyncTaskCompleted listener;



    public FavoritesRepository(Application application) {

        FavoritesDatabase favoritesDatabase = FavoritesDatabase.getInstance(application);
        movieFavoritesDao = favoritesDatabase.favoritesDao();
        allFavoriteMovies = movieFavoritesDao.getAllMovies();

    }

    public void insert(MovieFavorites movieFavorites) {
        new InsertMovieFavoriteAssyncTask(movieFavoritesDao).execute(movieFavorites);

    }

    public void delete(int id) {
        new DeleteMovieFavoriteAssyncTask(movieFavoritesDao).execute(id);
    }

    public LiveData<List<MovieFavorites>> getAllFavoriteMovies() {
        return allFavoriteMovies;
    }

    public void loadMoviebyId(int id, MovieDetailsActivity.OnAsyncTaskCompleted listener) {
        new LoadMovieFavoriteByIdAssyncTask(movieFavoritesDao, listener).execute(id);
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
    private static class DeleteMovieFavoriteAssyncTask extends AsyncTask<Integer, Void, Void> {

        private MovieFavoritesDao movieFavoritesDao;


        public DeleteMovieFavoriteAssyncTask(MovieFavoritesDao movieFavoritesDao) {
            this.movieFavoritesDao = movieFavoritesDao;
        }

        @Override
        protected Void doInBackground(Integer... movieId) {
            movieFavoritesDao.delete(movieId[0]);
            return null;
        }
    }


    // we need to perform database operations on background thread, need to do this manually
    private static class LoadMovieFavoriteByIdAssyncTask extends AsyncTask<Integer, Void, MovieFavorites> {

        private MovieFavoritesDao movieFavoritesDao;
        private MovieFavorites movieFavorites;
        private MovieDetailsActivity.OnAsyncTaskCompleted listener;
        private boolean doesmovieExists = false;

        public LoadMovieFavoriteByIdAssyncTask(MovieFavoritesDao movieFavoritesDao, MovieDetailsActivity.OnAsyncTaskCompleted listener) {
            this.movieFavoritesDao = movieFavoritesDao;
            this.listener = listener;
        }

        @Override
        protected MovieFavorites doInBackground(Integer... movieId) {
            this.movieFavorites = movieFavoritesDao.loadMovieById(movieId[0]);
            return movieFavorites;
        }

        @Override
        protected void onPostExecute(MovieFavorites movieFavorites) {
            doesmovieExists = movieFavorites != null;
            listener.onTaskCompleted(doesmovieExists);
        }
    }


}
