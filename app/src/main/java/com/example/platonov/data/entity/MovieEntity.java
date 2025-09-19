package com.example.platonov.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "movies",
        indices = {@Index("userId")}
)
public class MovieEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long userId;

    private String title;
    private int year;
    private String genre;
    private String posterUrl;
    private String synopsis;
    private boolean isFavorite;
    private float userRating;
    private String notes;

    // Нужен Room
    public MovieEntity() {}

    // Удобный конструктор (Room игнорирует)
    @Ignore
    public MovieEntity(String title, int year, String genre, String posterUrl,
                       String synopsis, boolean isFavorite, float userRating, String notes) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
        this.isFavorite = isFavorite;
        this.userRating = userRating;
        this.notes = notes;
    }

    // --- getters / setters, которые ждут твои фрагменты и адаптеры ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public float getUserRating() { return userRating; }
    public void setUserRating(float userRating) { this.userRating = userRating; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
