package com.example.platonov.data.repository;



import java.util.HashMap;
import java.util.Map;

public class GenreMapper {
    // Статическая карта: TMDb genre ID → человекочитаемое название
    public static final Map<Integer, String> TMDB_GENRE_MAP = new HashMap<>();

    static {
        TMDB_GENRE_MAP.put(28, "Экшн");
        TMDB_GENRE_MAP.put(12, "Приключения");
        TMDB_GENRE_MAP.put(16, "Анимация");
        TMDB_GENRE_MAP.put(35, "Комедия");
        TMDB_GENRE_MAP.put(80, "Криминал");
        TMDB_GENRE_MAP.put(99, "Документальное");
        TMDB_GENRE_MAP.put(18, "Научная фантастика");
        TMDB_GENRE_MAP.put(10751, "Семейное");
        TMDB_GENRE_MAP.put(14, "Фэнтези");
        TMDB_GENRE_MAP.put(36, "Историческое");
        TMDB_GENRE_MAP.put(27, "Ужасы");
        TMDB_GENRE_MAP.put(10402, "Музыкальное");
        TMDB_GENRE_MAP.put(9648, "Мистика");
        TMDB_GENRE_MAP.put(10749, "Романтическое");
        TMDB_GENRE_MAP.put(878, "Научная фантастика");
        TMDB_GENRE_MAP.put(10770, "ТВ фильм");
        TMDB_GENRE_MAP.put(53, "Триллер");
        TMDB_GENRE_MAP.put(10752, "Военный");
        TMDB_GENRE_MAP.put(37, "Вестерн");
        // … если нужно, добавьте другие (полный список на https://developers.themoviedb.org/3/genres/get-movie-list) …
    }
}
