package com.example.platonov.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Описывает один элемент массива "results" в ответе TMDb при поиске.
 */
public class TmdbMovieResponse {

    @SerializedName("id")
    private long tmdbId;

    @SerializedName("title")
    private String title;

    @SerializedName("release_date")
    private String releaseDate; // формат "YYYY-MM-DD"

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath; // нужно приклеить к baseURL, чтобы получить полный URL

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    // Геттеры

    public long getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }
}