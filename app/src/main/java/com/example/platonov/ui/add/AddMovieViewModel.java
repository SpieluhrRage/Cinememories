package com.example.platonov.ui.add;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel для экрана добавления фильма (Manual + Search).
 * Позволяет:
 *  1) Вставлять MovieEntity в локальную БД.
 *  2) Делать поиск по TMDb (возвращает LiveData<List<MovieEntity>>).
 */
public class AddMovieViewModel extends AndroidViewModel {

    private final MovieRepository repository;

    public AddMovieViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
    }

    /** Вставка фильма в локальную БД */
    public void insertMovie(MovieEntity movie) {
        repository.insertMovie(movie);
    }

    /** Обновление фильма (если нужно) */
    public void updateMovie(MovieEntity movie) {
        repository.updateMovie(movie);
    }

    /** Поиск фильмов в TMDb (конвертирует результат в List<MovieEntity>) */
    public LiveData<List<MovieEntity>> searchMovies(String query) {
        return repository.searchMoviesInTmdb(query);
    }
}