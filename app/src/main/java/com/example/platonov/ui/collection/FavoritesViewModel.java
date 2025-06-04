package com.example.platonov.ui.collection;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel для экрана "Избранное".
 * Держит LiveData со списком фильмов, у которых isFavorite = true.
 */
public class FavoritesViewModel extends AndroidViewModel {

    private final MovieRepository repository;
    private final LiveData<List<MovieEntity>> favoriteMovies;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
        favoriteMovies = repository.getFavoriteMovies();
    }

    /** Возвращает LiveData со списком избранных фильмов */
    public LiveData<List<MovieEntity>> getFavoriteMovies() {
        return favoriteMovies;
    }

    /** Обновление MovieEntity (чтобы можно было убирать из избранного) */
    public void updateMovie(MovieEntity movie) {
        repository.updateMovie(movie);
    }
}