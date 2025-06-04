package com.example.platonov.ui.detail;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.repository.MovieRepository;

/**
 * ViewModel для экрана деталей фильма.
 * Держит LiveData<MovieEntity> для одного movieId.
 */
public class MovieDetailViewModel extends AndroidViewModel {
    private final MovieRepository repository;

    public MovieDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
    }

    /** Возвращает LiveData<MovieEntity> по переданному ID */
    public LiveData<MovieEntity> getMovieById(long movieId) {
        return repository.getMovieById(movieId);
    }

    /** Обновить MovieEntity */
    public void updateMovie(MovieEntity movie) {
        repository.updateMovie(movie);
    }
}

