package com.example.platonov.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.platonov.BuildConfig;
import com.example.platonov.data.dao.MovieDao;
import com.example.platonov.data.database.AppDatabase;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.prefs.SessionManager;
import com.example.platonov.network.TmdbApiService;
import com.example.platonov.network.TmdbMovieResponse;
import com.example.platonov.network.TmdbRetrofitClient;
import com.example.platonov.network.TmdbSearchResult;

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
 * !!! Главное отличие: все выборки и вставки теперь привязаны к активному userId.
 */
public class MovieRepository {

    private final MovieDao movieDao;
    private final TmdbApiService tmdbApiService;
    private final SessionManager session;

    private static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    // Формат даты из TMDb: "yyyy-MM-dd"
    private final SimpleDateFormat tmdbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // Храним год как "yyyy"
    private final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public MovieRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        movieDao = db.movieDao();
        tmdbApiService = TmdbRetrofitClient.getInstance();
        session = new SessionManager(application);
    }

    /** Удобный помощник: актуальный ID пользователя */
    private long uid() {
        return session.getUserId();
    }

    /** Получить LiveData со списком всех фильмов ТЕКУЩЕГО пользователя */
    public LiveData<List<MovieEntity>> getAllMovies() {
        return movieDao.getAllMoviesForUser(uid());
    }

    /** Получить LiveData с одним фильмом по ID (без фильтра по userId — id уникален) */
    public LiveData<MovieEntity> getMovieById(long movieId) {
        return movieDao.getMovieById(movieId);
    }

    /** Получить LiveData списка избранных фильмов ТЕКУЩЕГО пользователя */
    public LiveData<List<MovieEntity>> getFavoriteMovies() {
        return movieDao.getFavoriteMoviesForUser(uid());
    }

    /** Получить LiveData списка фильмов с фильтрацией для ТЕКУЩЕГО пользователя */
    public LiveData<List<MovieEntity>> getMoviesByFilter(String genre, Integer yearFrom, Integer yearTo, Float minRating) {
        return movieDao.getMoviesByFilterForUser(genre, yearFrom, yearTo, minRating, uid());
    }

    /** Вставить новый фильм (асинхронно) — ВАЖНО: проставляем владельца */
    public void insertMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> {
            movie.setUserId(uid()); ; // привязка к текущему пользователю
            movieDao.insert(movie);
        });
    }

    /** Обновить фильм (асинхронно) — владелец уже задан при вставке */
    public void updateMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> movieDao.updateMovie(movie));
    }

    /** Удалить фильм (асинхронно) */
    public void deleteMovie(final MovieEntity movie) {
        databaseExecutor.execute(() -> movieDao.deleteMovie(movie));
    }

    /**
     * Поиск в TMDb. Возвращаем список MovieEntity-ПРЕДЛОЖЕНИЙ.
     * Мы сразу проставим userId, чтобы при прямой вставке не забыть;
     * но конечная вставка всё равно идёт через insertMovie().
     */
    public MutableLiveData<List<MovieEntity>> searchMoviesInTmdb(String query) {
        MutableLiveData<List<MovieEntity>> liveData = new MutableLiveData<>();

        Call<TmdbSearchResult> call = tmdbApiService.searchMovies(
                BuildConfig.TMDB_API_KEY,
                query,
                1,
                "ru-RU"
        );

        call.enqueue(new Callback<TmdbSearchResult>() {
            @Override
            public void onResponse(Call<TmdbSearchResult> call, Response<TmdbSearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TmdbMovieResponse> tmdbList = response.body().getResults();
                    List<MovieEntity> converted = new ArrayList<>();

                    long ownerId = uid();

                    for (TmdbMovieResponse item : tmdbList) {
                        String title = item.getTitle();

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
                            genreNames = android.text.TextUtils.join(", ", namesList);
                        }

                        String posterUrl = null;
                        if (item.getPosterPath() != null) {
                            posterUrl = "https://image.tmdb.org/t/p/w500" + item.getPosterPath();
                        }

                        String synopsis = item.getOverview();

                        MovieEntity movieEntity = new MovieEntity(
                                      // текущий пользователь
                                title,
                                yearInt,
                                genreNames,
                                posterUrl,
                                synopsis,
                                false,    // isFavorite
                                0f,       // userRating
                                ""       // notes
                        );

                        // ВАЖНО: отметим владельца прямо в кандидате
                        movieEntity.setUserId(ownerId);

                        converted.add(movieEntity);
                    }

                    liveData.postValue(converted);
                } else {
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
