package com.example.platonov;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private String year;
    private String genre;

    public Movie(String title, String year, String genre) {
        this.title = title;
        this.year = year;
        this.genre = genre;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        year = in.readString();
        genre = in.readString();
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

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(genre);
    }

    @Override
    public String toString() {
        return title + " (" + year + ") - " + genre;
    }
}