    package com.example.platonov;

    import android.os.Parcel;
    import android.os.Parcelable;

    public class Movie implements Parcelable {
        private long id;
        private String title;
        private String description;
        private String genre;
        private String posterUri;
        private String releaseDate; // Дата выхода фильма
        private String watchedDate; // Дата просмотра фильма

        // Конструктор для создания нового фильма (ID будет присвоен БД)
        public Movie(String title, String description, String genre, String posterUri, String releaseDate, String watchedDate) {
            this.id = -1;
            this.title = title;
            this.description = description;
            this.genre = genre;
            this.posterUri = posterUri;
            this.releaseDate = releaseDate;
            this.watchedDate = watchedDate;
        }

        // Более простой конструктор для нового фильма без дат по умолчанию
        public Movie(String title, String description, String genre, String posterUri) {
            this(title, description, genre, posterUri, null, null);
        }


        // Конструктор для создания фильма из данных БД (с ID и датами)
        public Movie(long id, String title, String description, String genre, String posterUri, String releaseDate, String watchedDate) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.genre = genre;
            this.posterUri = posterUri;
            this.releaseDate = releaseDate;
            this.watchedDate = watchedDate;
        }
        // Конструктор для создания фильма из данных БД (с ID, если даты могут быть null)
        public Movie(long id, String title, String description, String genre, String posterUri) {
            this(id, title, description, genre, posterUri, null, null);
        }


        // Getters
        public long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getGenre() { return genre; }
        public String getPosterUri() { return posterUri; }
        public String getReleaseDate() { return releaseDate; }
        public String getWatchedDate() { return watchedDate; }

        // Setters
        public void setId(long id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
        public void setDescription(String description) { this.description = description; }
        public void setGenre(String genre) { this.genre = genre; }
        public void setPosterUri(String posterUri) { this.posterUri = posterUri; }
        public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
        public void setWatchedDate(String watchedDate) { this.watchedDate = watchedDate; }

        // Parcelable реализация
        protected Movie(Parcel in) {
            id = in.readLong();
            title = in.readString();
            description = in.readString();
            genre = in.readString();
            posterUri = in.readString();
            releaseDate = in.readString(); // Читаем дату выхода
            watchedDate = in.readString(); // Читаем дату просмотра
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
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(genre);
            dest.writeString(posterUri);
            dest.writeString(releaseDate); // Пишем дату выхода
            dest.writeString(watchedDate); // Пишем дату просмотра
        }

        @Override
        public String toString() { return title; }
    }