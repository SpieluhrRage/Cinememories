package com.example.platonov.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Интерфейс Retrofit для TMDb API (Search Movie).
 * Документация: https://developers.themoviedb.org/3/search/search-movies
 */
public interface TmdbApiService {

    // Базовый URL: https://api.themoviedb.org/3/
    // Полный путь: https://api.themoviedb.org/3/search/movie

    @GET("search/movie")
    Call<TmdbSearchResult> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,         // название, которое ищем
            @Query("page") int page,              // страница (1, 2, …)
            @Query("language") String language     // пример: "en-US" или "ru-RU"
            // можно добавить &include_adult=false и т.д.
    );
}