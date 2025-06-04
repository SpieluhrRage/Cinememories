package com.example.platonov.data.entity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Сущность MovieEntity представляет одну запись фильма в локальной базе Room.
 */
@Entity(tableName = "movies")
public class MovieEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "year")
    private int year;

    @ColumnInfo(name = "genre")
    private String genre;

    @ColumnInfo(name = "poster_url")
    private String posterUrl;

    @ColumnInfo(name = "synopsis")
    private String synopsis;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;



    @ColumnInfo(name = "user_rating")
    private float userRating;

    @ColumnInfo(name = "notes")
    private String notes;

    // Конструктор для создания новой сущности (id заполняется Room автоматически)
    public MovieEntity(String title, int year, String genre, String posterUrl,
                       String synopsis, boolean isFavorite,
                       float userRating, String notes) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
        this.isFavorite = isFavorite;
        this.userRating = userRating;
        this.notes = notes;
    }

    // Геттеры и сеттеры для каждого поля

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
