package com.example.platonov.ui.search;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel для экрана «Поиск/Фильтрация».
 * Принимает набор параметров и возвращает отфильтрованный список.
 */
public class SearchFilterViewModel extends AndroidViewModel {

    private final MovieRepository repository;

    public SearchFilterViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
    }

    /**
     * Возвращает LiveData списка фильмов по заданным критериям:
     * genre, yearFrom, yearTo, minRating.
     * Если какой-то параметр = null, фильтрация по нему не применяется.
     */
    public LiveData<List<MovieEntity>> getFilteredMovies(String genre,
                                                         Integer yearFrom,
                                                         Integer yearTo,
                                                         Float minRating) {
        return repository.getMoviesByFilter(genre, yearFrom, yearTo, minRating);
    }
}