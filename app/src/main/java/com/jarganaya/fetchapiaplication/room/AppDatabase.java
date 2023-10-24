package com.jarganaya.fetchapiaplication.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jarganaya.fetchapiaplication.room.entity.FavoriteMovieDao;
import com.jarganaya.fetchapiaplication.room.entity.FavoriteMovieEntity;

@Database(entities = {
        FavoriteMovieEntity.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteMovieDao favoriteMovieDao();
}
