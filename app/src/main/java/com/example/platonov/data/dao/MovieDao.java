package com.example.platonov.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.platonov.data.entity.MovieEntity;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert
    long insert(MovieEntity movie);

    @Update
    int updateMovie(MovieEntity movie);

    @Delete
    int deleteMovie(MovieEntity movie);

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    LiveData<MovieEntity> getMovieById(long movieId);

    @Query("SELECT * FROM movies WHERE userId = :uid ORDER BY id DESC")
    LiveData<List<MovieEntity>> getAllMoviesForUser(long uid);

    @Query("SELECT * FROM movies WHERE isFavorite = 1 AND userId = :uid ORDER BY id DESC")
    LiveData<List<MovieEntity>> getFavoriteMoviesForUser(long uid);

    @Query("SELECT * FROM movies " +
            "WHERE userId = :uid " +
            "AND (:genre IS NULL OR genre LIKE '%' || :genre || '%') " +
            "AND (:yearFrom IS NULL OR year >= :yearFrom) " +
            "AND (:yearTo   IS NULL OR year <= :yearTo) " +
            "AND (:minRating IS NULL OR userRating >= :minRating) " +
            "ORDER BY id DESC")
    LiveData<List<MovieEntity>> getMoviesByFilterForUser(
            String genre, Integer yearFrom, Integer yearTo, Float minRating, long uid
    );
}
