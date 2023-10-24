package com.jarganaya.fetchapiaplication.room.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoriteMovieDao {
    @Insert
    void insert(FavoriteMovieEntity favoriteMovie);

    @Query("SELECT * FROM favorite_movies")
    List<FavoriteMovieEntity> getAll();

    @Query("DELETE FROM favorite_movies WHERE movie_id = :movieId")
    void delete(int movieId);
}
