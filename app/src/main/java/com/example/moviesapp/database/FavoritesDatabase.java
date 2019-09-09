package com.example.moviesapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MovieFavorites.class}, version = 1)
public abstract class FavoritesDatabase extends RoomDatabase {

    // make database singleton so that we can´t instantiate more than once
    private static FavoritesDatabase favoritesDatabaseInstance;

    // synchronized means only one thread at a time can access this method, this way we don´t create two instances of database when two threads
    // try to access this at the same time
    public static synchronized FavoritesDatabase getInstance(Context context) {
        if (favoritesDatabaseInstance == null) {
            favoritesDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(), FavoritesDatabase.class, "favorites_database")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return favoritesDatabaseInstance;
    }

    public abstract MovieFavoritesDao favoritesDao();

/*    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };*/

}
