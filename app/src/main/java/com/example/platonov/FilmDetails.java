package com.example.platonov;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.platonov.db.DatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class FilmDetails extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView genreTextView;
    private TextView yearTextView;
    private ImageView posterImageView;
    private Button backButton;
    private Button deleteButton;
    private Button editButton; // Добавили кнопку редактирования
    private TextView watchedDateTextView;
    private Button setDateButton;

    private Movie currentMovie;
    private DatabaseHelper dbHelper;

    private int watchedYear = 0;
    private int watchedMonth = 0;
    private int watchedDay = 0;

    public static final String EXTRA_DELETED_MOVIE_ID = "com.example.platonov.DELETED_MOVIE_ID";
    public static final String EXTRA_UPDATED_MOVIE_ID = "com.example.platonov.UPDATED_MOVIE_ID";
    private static final int EDIT_MOVIE_REQUEST = 3; // Код запроса для редактирования


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);

        dbHelper = new DatabaseHelper(this);

        titleTextView = findViewById(R.id.movieTitle);
        descriptionTextView = findViewById(R.id.movieDescription);
        genreTextView = findViewById(R.id.movieGenre);
        yearTextView = findViewById(R.id.movieYear);
        posterImageView = findViewById(R.id.moviePoster);
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton); // Находим кнопку
        watchedDateTextView = findViewById(R.id.watchedDateTextView);
        setDateButton = findViewById(R.id.setDateButton);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            currentMovie = intent.getParcelableExtra("movie");
            if (currentMovie != null) {
                populateMovieDetails();
                backButton.setOnClickListener(v -> finish());
                deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
                editButton.setOnClickListener(v -> startEditActivity()); // Устанавливаем слушатель
                setDateButton.setOnClickListener(v -> showDatePickerDialog());
            } else {
                handleDataError("Movie object received from Intent is null");
            }
        } else {
            handleDataError("Intent is null or does not contain 'movie' extra");
        }
    }

    private void startEditActivity() {
        if (currentMovie != null) {
            Intent editIntent = new Intent(FilmDetails.this, AddFilm.class);
            editIntent.putExtra(AddFilm.EXTRA_MOVIE_TO_EDIT, currentMovie);
            startActivityForResult(editIntent, EDIT_MOVIE_REQUEST);
        } else {
            Toast.makeText(this, "Ошибка: нет данных для редактирования", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateMovieDetails() {
        if (currentMovie == null) return;
        titleTextView.setText(currentMovie.getTitle());
        descriptionTextView.setText(currentMovie.getDescription());
        yearTextView.setText("Год: (нет данных)");
        if (currentMovie.getReleaseDate() != null && !currentMovie.getReleaseDate().isEmpty()) {
            yearTextView.setText("Год выхода: " + currentMovie.getReleaseDate());
        } else {
            yearTextView.setText("Год выхода: не указан");
        }// Заглушка

        if (currentMovie.getGenre() != null) {
            genreTextView.setText("Жанр: " + currentMovie.getGenre());
        } else {
            genreTextView.setText("Жанр: Не указан");
        }

        String posterUriString = currentMovie.getPosterUri();
        if (posterUriString != null && !posterUriString.isEmpty()) {
            try {
                posterImageView.setImageURI(Uri.parse(posterUriString));
            } catch (Exception e) {
                Log.e("FilmDetails", "Error loading poster URI: " + posterUriString, e);
                posterImageView.setImageResource(R.drawable.poster); // Заглушка по умолчанию
            }
        } else {
            posterImageView.setImageResource(R.drawable.poster); // Заглушка по умолчанию
        }
        if (currentMovie.getWatchedDate() != null && !currentMovie.getWatchedDate().isEmpty()) {
            watchedDateTextView.setText("Дата просмотра: " + currentMovie.getWatchedDate());
            // Инициализация watchedYear, watchedMonth, watchedDay для DatePicker
            try {
                String[] dateParts = currentMovie.getWatchedDate().split("\\.");
                if (dateParts.length == 3) {
                    watchedDay = Integer.parseInt(dateParts[0]);
                    watchedMonth = Integer.parseInt(dateParts[1]) - 1; // месяцы 0-11
                    watchedYear = Integer.parseInt(dateParts[2]);
                }
            } catch (Exception e) {
                Log.e("FilmDetails", "Error parsing watched date for DatePicker init", e);
            }
        } else {
            watchedDateTextView.setText("Дата просмотра: не указана");
            watchedDay = 0; watchedMonth = 0; watchedYear = 0; // Сброс для DatePicker
        }

    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int initialYear = (watchedYear != 0) ? watchedYear : c.get(Calendar.YEAR);
        int initialMonth = (watchedMonth != 0) ? watchedMonth : c.get(Calendar.MONTH);
        int initialDay = (watchedDay != 0) ? watchedDay : c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    watchedYear = year;
                    watchedMonth = monthOfYear;
                    watchedDay = dayOfMonth;
                    String selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%d", watchedDay, (watchedMonth + 1), watchedYear);
                    watchedDateTextView.setText("Дата просмотра: " + selectedDate);
                    if (currentMovie != null) {
                        currentMovie.setWatchedDate(selectedDate); // Устанавливаем дату в объект
                        int updatedRows = dbHelper.updateMovie(currentMovie); // Обновляем в БД
                        if (updatedRows > 0) {
                            Toast.makeText(FilmDetails.this, "Дата просмотра сохранена", Toast.LENGTH_SHORT).show();
                            // Уведомляем AllMoviesFragment, что данные фильма изменились
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(FilmDetails.EXTRA_UPDATED_MOVIE_ID, currentMovie.getId());
                            setResult(Activity.RESULT_OK, resultIntent);
                        } else {
                            Toast.makeText(FilmDetails.this, "Ошибка сохранения даты просмотра", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(FilmDetails.this, "Дата просмотра установлена", Toast.LENGTH_SHORT).show();
                }, initialYear, initialMonth, initialDay);
        datePickerDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        if (currentMovie == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить фильм \"" + currentMovie.getTitle() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Удалить", (dialog, which) -> performDelete())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void performDelete() {
        if (currentMovie == null || currentMovie.getId() == -1) {
            Toast.makeText(this, "Невозможно удалить: ID фильма неизвестен", Toast.LENGTH_SHORT).show();
            return;
        }
        int deletedRows = dbHelper.deleteMovie(currentMovie.getId());
        if (deletedRows > 0) {
            Toast.makeText(this, "Фильм \"" + currentMovie.getTitle() + "\" удален", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_DELETED_MOVIE_ID, currentMovie.getId());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Ошибка удаления фильма из базы данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDataError(String logMessage) {
        Log.e("FilmDetails", logMessage);
        Toast.makeText(this, "Ошибка загрузки данных фильма", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_MOVIE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Movie updatedMovie = data.getParcelableExtra(AddFilm.EXTRA_SAVED_MOVIE);
            if (updatedMovie != null) {
                currentMovie = updatedMovie; // Обновляем текущий фильм в FilmDetails
                populateMovieDetails();      // Обновляем UI
                Toast.makeText(this, "Фильм успешно обновлен", Toast.LENGTH_SHORT).show();

                // Устанавливаем результат для AllMoviesFragment, чтобы он обновил список
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_UPDATED_MOVIE_ID, currentMovie.getId());
                setResult(Activity.RESULT_OK, resultIntent);
                // Не закрываем FilmDetails автоматически, пользователь сам выйдет
            }
        }
    }
}