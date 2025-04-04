package com.example.platonov; // Замени на свой пакет

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private String description;
    private String genre; // Добавим поле для жанра
    private String posterUri; // Строка для URI постера

    // Конструкторы (можешь добавить/изменить по необходимости)
    public Movie(String title, String description, String genre, String posterUri) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.posterUri = posterUri;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getPosterUri() { return posterUri; }

    // Setters (если нужны для редактирования)
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPosterUri(String posterUri) { this.posterUri = posterUri; }


    // --- Parcelable реализация ---
    protected Movie(Parcel in) {
        title = in.readString();
        description = in.readString();
        genre = in.readString();
        posterUri = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(genre);
        dest.writeString(posterUri);
    }

    // Переопределим toString() для удобства отображения в простом ArrayAdapter, если понадобится
    @Override
    public String toString() {
        return title; // Показываем только название в простом списке
    }
}