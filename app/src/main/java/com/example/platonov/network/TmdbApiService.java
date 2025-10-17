package com.example.platonov.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Документация: https://developers.themoviedb.org/3/search/search-movies
 */
public interface TmdbApiService {

    // https://api.themoviedb.org/3/search/movie

    @GET("search/movie")
    Call<TmdbSearchResult> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page,
            @Query("language") String language

    );
}