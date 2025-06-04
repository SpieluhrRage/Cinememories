package com.example.platonov.ui.collection;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.repository.MovieRepository;

import java.util.List;

/**
 * ViewModel для экрана «Коллекция фильмов».
 * Держит LiveData со списком всех фильмов и предоставляет методы
 * для операций CRUD через репозиторий.
 */
public class MovieListViewModel extends AndroidViewModel {

    private final MovieRepository repository;
    private final LiveData<List<MovieEntity>> allMovies;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        // Создаём репозиторий, передавая контекст приложения
        repository = new MovieRepository(application);
        // Получаем LiveData со всем списком фильмов
        allMovies = repository.getAllMovies();
    }

    /**
     * Возвращает LiveData со списком всех фильмов.
     * Фрагмент будет наблюдать за этим списком и автоматически
     * обновлять UI, когда данные в базе изменятся.
     */
    public LiveData<List<MovieEntity>> getAllMovies() {
        return allMovies;
    }

    /**
     * Запросить фильтрацию по параметрам (для будущей функциональности).
     * Если параметры равны null, фильтрация не применяется по этому полю.
     */
    public LiveData<List<MovieEntity>> getMoviesByFilter(
            String genre, Integer yearFrom, Integer yearTo, Float minRating) {
        return repository.getMoviesByFilter(genre, yearFrom, yearTo, minRating);
    }

    /**
     * Вставить новый фильм (передать команду в репозиторий).
     */
    public void insertMovie(MovieEntity movie) {
        repository.insertMovie(movie);
    }

    /**
     * Обновить существующий фильм (например, пометить «избранным» или изменить заметки).
     */
    public void updateMovie(MovieEntity movie) {
        repository.updateMovie(movie);
    }

    /**
     * Удалить фильм из базы.
     */
    public void deleteMovie(MovieEntity movie) {
        repository.deleteMovie(movie);
    }
}

