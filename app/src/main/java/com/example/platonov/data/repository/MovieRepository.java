package com.example.platonov.data.repository;


import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.platonov.data.dao.MovieDao;
import com.example.platonov.data.database.AppDatabase;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.network.TmdbApiService;
import com.example.platonov.network.TmdbRetrofitClient;
import com.example.platonov.network.TmdbMovieResponse;
import com.example.platonov.network.TmdbSearchResult;
import com.example.platonov.BuildConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Репозиторий для работы с MovieEntity и TMDb API.
 */
public class MovieRepository {

    private final MovieDao movieDao;
    private final LiveData<List<MovieEntity>> allMovies;
    private final LiveData<List<MovieEntity>> favoriteMovies;
    private final TmdbApiService tmdbApiService;

    private static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    // Формат даты из TMDb: "yyyy-MM-dd"
    private final SimpleDateFormat tmdbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // Формат, в котором вы хотите хранить год или дату (например "yyyy")
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    // TMDb API_KEY (поместите сюда ваш ключ)


    public MovieRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getAllMovies();
        favoriteMovies = movieDao.getFavoriteMovies();
        tmdbApiService = TmdbRetrofitClient.getInstance();
    }

    /** Получить LiveData со списком всех фильмов */
    public LiveData<List<MovieEntity>> getAllMovies() {
        return allMovies;
    }

    /** Получить LiveData с одним фильмом по ID */
    public LiveData<MovieEntity> getMovieById(long movieId) {
        return movieDao.getMovieById(movieId);
    }

    /** Получить LiveData списка избранных фильмов */
    public LiveData<List<MovieEntity>> getFavoriteMovies() {
        return favoriteMovies;
    }

    /** Получить LiveData списка фильмов с фильтрацией */
    public LiveData<List<MovieEntity>> getMoviesByFilter(String genre, Integer yearFrom, Integer yearTo, Float minRating) {
        return movieDao.getMoviesByFilter(genre, yearFrom, yearTo, minRating);
    }

    /** Вставить новый фильм (асинхронно) */
    public void insertMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> movieDao.insertMovie(movie));
    }

    /** Обновить фильм (асинхронно) */
    public void updateMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> movieDao.updateMovie(movie));
    }

    /** Удалить фильм (асинхронно) */
    public void deleteMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> movieDao.deleteMovie(movie));
    }

    /**
     * Поиск фильма по title в TMDb.
     * Возвращает MutableLiveData, на которую можно подписаться (ViewModel).
     */
    public MutableLiveData<List<MovieEntity>> searchMoviesInTmdb(String query) {
        MutableLiveData<List<MovieEntity>> liveData = new MutableLiveData<>();

        // Делаем асинхронный запрос к TMDb
        Call<TmdbSearchResult> call = tmdbApiService.searchMovies(
                BuildConfig.TMDB_API_KEY,
                query,
                1,         // берем первую страницу
                "ru-RU"    // можно “en-US” или другой язык
        );
        call.enqueue(new Callback<TmdbSearchResult>() {
            @Override
            public void onResponse(Call<TmdbSearchResult> call, Response<TmdbSearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TmdbMovieResponse> tmdbList = response.body().getResults();
                    List<MovieEntity> converted = new ArrayList<>();

                    // Конвертируем список TmdbMovieResponse -> MovieEntity
                    for (TmdbMovieResponse item : tmdbList) {
                        String title = item.getTitle();

                        // Преобразуем release_date -> год (int)
                        int yearInt = 0;
                        String rd = item.getReleaseDate();
                        if (rd != null && !rd.isEmpty()) {
                            try {
                                Date date = tmdbDateFormat.parse(rd);
                                String yearStr = yearFormat.format(date);
                                yearInt = Integer.parseInt(yearStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        // Жанры (только ID сейчас). Если надо строку, можно преобразовать в "Action, Drama" вручную.
                        String genreNames = "";
                        List<Integer> genreIds = item.getGenreIds();
                        if (genreIds != null && !genreIds.isEmpty()) {
                            List<String> namesList = new ArrayList<>();
                            for (Integer id : genreIds) {
                                String name = GenreMapper.TMDB_GENRE_MAP.get(id);
                                if (name != null) {
                                    namesList.add(name);
                                }
                            }
                            // Объединяем список названий в одну строку через запятую
                            genreNames = android.text.TextUtils.join(", ", namesList);
                        }

                        // Постер: TMDb возвращает только относительный путь, нужно собрать полный URL:
                        //   https://image.tmdb.org/t/p/w500/{poster_path}
                        // или другой размер: w200, w300, original и т. д.
                        String posterUrl = null;
                        if (item.getPosterPath() != null) {
                            posterUrl = "https://image.tmdb.org/t/p/w500" + item.getPosterPath();
                        }

                        String synopsis = item.getOverview(); // описание из TMDb

                        // Создаём MovieEntity. Для полей isFavorite, isWatched, userRating, notes поставим значения по умолчанию
                        MovieEntity movieEntity = new MovieEntity(
                                title,
                                yearInt,
                                genreNames,
                                posterUrl,
                                synopsis,
                                false,    // isFavorite
                                0f,       // userRating
                                ""        // notes
                        );
                        converted.add(movieEntity);
                    }

                    // Публикуем результат
                    liveData.postValue(converted);
                } else {
                    // Если ответ пустой или ошибка, публикуем null или пустой список
                    liveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<TmdbSearchResult> call, Throwable t) {
                t.printStackTrace();
                liveData.postValue(new ArrayList<>());
            }
        });

        return liveData;
    }
}