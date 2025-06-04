package com.example.platonov.network;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Описывает обёртку-ответ от /search/movie TMDb.
 */
public class TmdbSearchResult {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<TmdbMovieResponse> results;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    // Геттеры

    public int getPage() {
        return page;
    }

    public List<TmdbMovieResponse> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}