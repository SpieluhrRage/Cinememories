package com.example.platonov.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.platonov.Movie;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "filmoteka.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_POSTER_URI = "poster_uri";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_WATCHED_DATE = "watched_date";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MOVIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_GENRE + " TEXT, " +
                    COLUMN_POSTER_URI + " TEXT, " + // Запятая здесь
                    COLUMN_RELEASE_DATE + " TEXT, " + // Новое поле
                    COLUMN_WATCHED_DATE + " TEXT" +   // Новое поле
                    ");";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database table: " + TABLE_MOVIES + " with version " + DATABASE_VERSION);
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Простая миграция для добавления новых столбцов, если они отсутствуют
        if (oldVersion < 2) {
            // Добавляем столбец для даты выхода, если его нет
            try {
                db.execSQL("ALTER TABLE " + TABLE_MOVIES + " ADD COLUMN " + COLUMN_RELEASE_DATE + " TEXT;");
                Log.i(TAG, "Upgraded table " + TABLE_MOVIES + ": added column " + COLUMN_RELEASE_DATE);
            } catch (Exception e) {
                Log.e(TAG, "Error adding column " + COLUMN_RELEASE_DATE + ": " + e.getMessage());
            }
            // Добавляем столбец для даты просмотра, если его нет
            try {
                db.execSQL("ALTER TABLE " + TABLE_MOVIES + " ADD COLUMN " + COLUMN_WATCHED_DATE + " TEXT;");
                Log.i(TAG, "Upgraded table " + TABLE_MOVIES + ": added column " + COLUMN_WATCHED_DATE);
            } catch (Exception e) {
                Log.e(TAG, "Error adding column " + COLUMN_WATCHED_DATE + ": " + e.getMessage());
            }
        }
        // Если нужна более сложная миграция или пересоздание таблицы при определенных условиях:
        // else {
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        // onCreate(db);
        // }
    }

    public Movie findMovieByTitle(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Movie movie = null;
        String selection = COLUMN_TITLE + " = ?";
        String[] selectionArgs = { title };
        Cursor cursor = db.query(TABLE_MOVIES, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int descIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int genreIndex = cursor.getColumnIndex(COLUMN_GENRE);
            int uriIndex = cursor.getColumnIndex(COLUMN_POSTER_URI);
            int releaseDateIndex = cursor.getColumnIndex(COLUMN_RELEASE_DATE);
            int watchedDateIndex = cursor.getColumnIndex(COLUMN_WATCHED_DATE);

            if (idIndex != -1 && titleIndex != -1) { // Основные поля должны быть
                long id = cursor.getLong(idIndex);
                String movieTitle = cursor.getString(titleIndex);
                String description = descIndex != -1 ? cursor.getString(descIndex) : null;
                String genre = genreIndex != -1 ? cursor.getString(genreIndex) : null;
                String posterUri = uriIndex != -1 ? cursor.getString(uriIndex) : null;
                String releaseDate = releaseDateIndex != -1 ? cursor.getString(releaseDateIndex) : null;
                String watchedDate = watchedDateIndex != -1 ? cursor.getString(watchedDateIndex) : null;

                movie = new Movie(id, movieTitle, description, genre, posterUri, releaseDate, watchedDate);
                Log.d(TAG, "Movie found by title: " + movieTitle);
            } else {
                Log.e(TAG, "Error getting essential column indices during find by title.");
            }
            cursor.close();
        } else {
            Log.d(TAG, "Movie not found by title: " + title);
        }
        db.close();
        return movie;
    }

    public int updateMovie(Movie movie) {
        if (movie.getId() == -1) {
            Log.e(TAG, "Cannot update movie without a valid ID.");
            return 0;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_DESCRIPTION, movie.getDescription());
        values.put(COLUMN_GENRE, movie.getGenre());
        values.put(COLUMN_POSTER_URI, movie.getPosterUri());
        values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(COLUMN_WATCHED_DATE, movie.getWatchedDate());

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(movie.getId()) };
        int updatedRows = db.update(TABLE_MOVIES, values, selection, selectionArgs);
        db.close();
        Log.i(TAG, "Updated " + updatedRows + " movie(s) with ID: " + movie.getId());
        return updatedRows;
    }

    public long addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_DESCRIPTION, movie.getDescription());
        values.put(COLUMN_GENRE, movie.getGenre());
        values.put(COLUMN_POSTER_URI, movie.getPosterUri());
        values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(COLUMN_WATCHED_DATE, movie.getWatchedDate());

        long newRowId = db.insert(TABLE_MOVIES, null, values);
        db.close();
        Log.i(TAG, "Movie added with ID: " + newRowId + ", Title: " + movie.getTitle());
        return newRowId;
    }

    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COLUMN_TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int descIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int genreIndex = cursor.getColumnIndex(COLUMN_GENRE);
            int uriIndex = cursor.getColumnIndex(COLUMN_POSTER_URI);
            int releaseDateIndex = cursor.getColumnIndex(COLUMN_RELEASE_DATE);
            int watchedDateIndex = cursor.getColumnIndex(COLUMN_WATCHED_DATE);

            if (idIndex == -1 || titleIndex == -1) {
                Log.e(TAG, "Essential column indices not found in getAllMovies!");
                cursor.close();
                db.close();
                return movieList;
            }

            do {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String description = descIndex != -1 ? cursor.getString(descIndex) : null;
                String genre = genreIndex != -1 ? cursor.getString(genreIndex) : null;
                String posterUri = uriIndex != -1 ? cursor.getString(uriIndex) : null;
                String releaseDate = releaseDateIndex != -1 ? cursor.getString(releaseDateIndex) : null;
                String watchedDate = watchedDateIndex != -1 ? cursor.getString(watchedDateIndex) : null;
                Movie movie = new Movie(id, title, description, genre, posterUri, releaseDate, watchedDate);
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        Log.d(TAG, "Fetched " + movieList.size() + " movies from DB");
        return movieList;
    }

    public int deleteMovie(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int deletedRows = db.delete(TABLE_MOVIES, selection, selectionArgs);
        db.close();
        Log.i(TAG, "Deleted " + deletedRows + " movie(s) with ID: " + id);
        return deletedRows;
    }
}