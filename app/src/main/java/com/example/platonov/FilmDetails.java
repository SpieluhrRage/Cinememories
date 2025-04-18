package com.example.platonov;

import android.app.Activity;
import android.app.DatePickerDialog; // Импорт DatePickerDialog
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker; // Импорт DatePicker
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar; // Импорт Calendar
import java.util.Locale;  // Импорт Locale

public class FilmDetails extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView genreTextView;
    private ImageView posterImageView;
    private Button backButton;
    private Button deleteButton;
    // Новые элементы для даты просмотра
    private TextView watchedDateTextView;
    private Button setDateButton;

    private Movie currentMovie;

    // Переменные для хранения последней выбранной даты
    private int watchedYear = 0;
    private int watchedMonth = 0; // 0-11
    private int watchedDay = 0;

    public static final String EXTRA_DELETED_MOVIE_TITLE = "com.example.platonov.DELETED_MOVIE_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_film_details);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        // Находим все View
        titleTextView = findViewById(R.id.movieTitle);
        descriptionTextView = findViewById(R.id.movieDescription);
        genreTextView = findViewById(R.id.movieGenre);
        posterImageView = findViewById(R.id.moviePoster);
        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);
        watchedDateTextView = findViewById(R.id.watchedDateTextView); // Находим TextView для даты
        setDateButton = findViewById(R.id.setDateButton);         // Находим Button для даты

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            currentMovie = intent.getParcelableExtra("movie");

            if (currentMovie != null) {
                populateMovieDetails(); // Вынесем заполнение данных в отдельный метод

                // Настраиваем слушателей кнопок
                backButton.setOnClickListener(v -> finish());
                deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
                // Добавляем слушатель для кнопки даты
                setDateButton.setOnClickListener(v -> showDatePickerDialog());

                // TODO: Загрузить сохраненную дату просмотра из currentMovie, если она там есть
                // и обновить watchedYear, watchedMonth, watchedDay и watchedDateTextView

            } else {
                handleDataError("Movie object received from Intent is null");
            }
        } else {
            handleDataError("Intent is null or does not contain 'movie' extra");
        }
    }

    // Метод для заполнения деталей фильма
    private void populateMovieDetails() {
        titleTextView.setText(currentMovie.getTitle());
        descriptionTextView.setText(currentMovie.getDescription());

        if (genreTextView != null && currentMovie.getGenre() != null) {
            genreTextView.setText("Жанр: " + currentMovie.getGenre());
        } else if (genreTextView != null) {
            genreTextView.setText("Жанр: Не указан");
        }

        String posterUriString = currentMovie.getPosterUri();
        if (posterUriString != null && !posterUriString.isEmpty()) {
            try {
                Uri posterUri = Uri.parse(posterUriString);
                posterImageView.setImageURI(posterUri);
            } catch (Exception e) {
                Log.e("FilmDetails", "Error loading poster URI: " + posterUriString, e);
                posterImageView.setImageResource(R.drawable.poster);
            }
        } else {
            posterImageView.setImageResource(R.drawable.poster);
        }

        // TODO: Отобразить сохраненную дату просмотра, если она есть в currentMovie
        // String savedDate = currentMovie.getWatchedDate(); // Предполагаем наличие метода
        // if (savedDate != null && !savedDate.isEmpty()) {
        //     watchedDateTextView.setText("Дата просмотра: " + savedDate);
        // Тут же нужно распарсить savedDate и обновить watchedYear, watchedMonth, watchedDay
        // } else {
        watchedDateTextView.setText("Дата просмотра: не указана");
        // }
    }


    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int initialYear = (watchedYear != 0) ? watchedYear : c.get(Calendar.YEAR);
        int initialMonth = (watchedMonth != 0) ? watchedMonth : c.get(Calendar.MONTH);
        int initialDay = (watchedDay != 0) ? watchedDay : c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        watchedYear = year;
                        watchedMonth = monthOfYear;
                        watchedDay = dayOfMonth;

                        String selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%d", watchedDay, (watchedMonth + 1), watchedYear);
                        watchedDateTextView.setText("Дата просмотра: " + selectedDate);

                        Toast.makeText(FilmDetails.this, "Дата просмотра установлена", Toast.LENGTH_SHORT).show();
                    }
                }, initialYear, initialMonth, initialDay);

        datePickerDialog.show();
    }


    // Метод для показа AlertDialog (остается без изменений)
    private void showDeleteConfirmationDialog() {
        if (currentMovie == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить фильм \"" + currentMovie.getTitle() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Удалить", (dialog, whichButton) -> performDelete())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void performDelete() {
        if (currentMovie == null) return;
        Log.d("FilmDetails", "Имитация удаления фильма: " + currentMovie.getTitle());
        Toast.makeText(this, "Фильм \"" + currentMovie.getTitle() + "\" удален", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_DELETED_MOVIE_TITLE, currentMovie.getTitle());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    // Обработка ошибок (остается без изменений)
    private void handleDataError(String logMessage) {
        Log.e("FilmDetails", logMessage);
        Toast.makeText(this, "Ошибка загрузки данных фильма", Toast.LENGTH_LONG).show();
        finish();
    }
}