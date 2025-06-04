package com.example.platonov.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.platonov.data.entity.MovieEntity;

import java.util.List;

/**
 * MovieDao содержит методы для доступа к таблице movies в локальной базе Room.
 */
@Dao
public interface MovieDao {

    // 1. Получить все фильмы, отсортированные по названию
    @Query("SELECT * FROM movies ORDER BY title ASC")
    LiveData<List<MovieEntity>> getAllMovies();

    // 2. Получить фильм по ID (для экрана деталей)
    @Query("SELECT * FROM movies WHERE id = :movieId")
    LiveData<MovieEntity> getMovieById(long movieId);

    // 3. Получить все фильмы, отмеченные как избранные
    @Query("SELECT * FROM movies WHERE is_favorite = 1 ORDER BY title ASC")
    LiveData<List<MovieEntity>> getFavoriteMovies();

    // 4. Вставить новый фильм в базу
    @Insert
    long insertMovie(MovieEntity movie);

    // 5. Обновить данные фильма (изменить флаги, заметки, рейтинг и т. д.)
    @Update
    void updateMovie(MovieEntity movie);

    // 6. Удалить фильм из базы
    @Delete
    void deleteMovie(MovieEntity movie);

    // 7. Получить список фильмов с учётом фильтрации
    @Query("SELECT * FROM movies " +
            "WHERE (:genre IS NULL OR genre LIKE '%' || :genre || '%') " +
            "AND (:yearFrom IS NULL OR year >= :yearFrom) " +
            "AND (:yearTo IS NULL OR year <= :yearTo) " +
            "AND (:minRating IS NULL OR user_rating >= :minRating) " +
            "ORDER BY title ASC")
    LiveData<List<MovieEntity>> getMoviesByFilter(String genre, Integer yearFrom, Integer yearTo, Float minRating);
}