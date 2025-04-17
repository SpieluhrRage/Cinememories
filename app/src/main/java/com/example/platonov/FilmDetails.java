package com.example.platonov;

import android.app.Activity; // Для RESULT_OK
import android.content.DialogInterface; // Для AlertDialog listener
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // Для View.OnClickListener
import android.widget.Button; // Для Button
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog; // Для AlertDialog
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FilmDetails extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView genreTextView;
    private ImageView posterImageView;
    private Button backButton;
    private Button deleteButton; // Добавляем кнопку

    private Movie currentMovie; // Сохраняем текущий фильм

    // Ключ для передачи результата
    public static final String EXTRA_DELETED_MOVIE_TITLE = "com.example.platonov.DELETED_MOVIE_TITLE";
    // Рекомендуется использовать ID, если он есть:
    // public static final String EXTRA_DELETED_MOVIE_ID = "com.example.platonov.DELETED_MOVIE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Можно временно отключить, если мешает
        setContentView(R.layout.activity_film_details);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        titleTextView = findViewById(R.id.movieTitle);
        descriptionTextView = findViewById(R.id.movieDescription);
        genreTextView = findViewById(R.id.movieGenre); // Убедись, что ID верный
        posterImageView = findViewById(R.id.moviePoster);
        backButton = findViewById(R.id.backButton); // Находим кнопку Назад
        deleteButton = findViewById(R.id.deleteButton); // Находим кнопку Удалить

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            currentMovie = intent.getParcelableExtra("movie"); // Сохраняем в переменную класса

            if (currentMovie != null) {
                // Заполняем View данными
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

                // --- Настраиваем слушателей кнопок ---
                backButton.setOnClickListener(v -> finish()); // Просто закрываем Activity

                deleteButton.setOnClickListener(v -> {
                    // Показываем диалог подтверждения при нажатии на Удалить
                    showDeleteConfirmationDialog();
                });

            } else {
                handleDataError("Movie object received from Intent is null");
            }
        } else {
            handleDataError("Intent is null or does not contain 'movie' extra");
        }
    }

    // Метод для показа AlertDialog
    private void showDeleteConfirmationDialog() {
        if (currentMovie == null) return; // На всякий случай

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение удаления")
                .setMessage("Вы уверены, что хотите удалить фильм \"" + currentMovie.getTitle() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Действия при подтверждении удаления
                        performDelete();
                    }
                })
                .setNegativeButton("Отмена", null) // Отмена - ничего не делаем, диалог закроется
                .show();
    }

    // Метод для выполнения удаления и возврата результата
    private void performDelete() {
        if (currentMovie == null) return;

        // TODO: Реализуй реальное удаление из твоего хранилища (БД, SharedPreferences, файл и т.д.)
        // Например: DatabaseHelper dbHelper = new DatabaseHelper(this);
        // dbHelper.deleteMovie(currentMovie.getId()); // Если у Movie есть ID
        Log.d("FilmDetails", "Имитация удаления фильма: " + currentMovie.getTitle());

        Toast.makeText(this, "Фильм \"" + currentMovie.getTitle() + "\" удален", Toast.LENGTH_SHORT).show();

        // --- Возвращаем результат в предыдущую Activity (MainScreen/AllMoviesFragment) ---
        Intent resultIntent = new Intent();
        // Передаем идентификатор удаленного фильма (лучше использовать ID)
        resultIntent.putExtra(EXTRA_DELETED_MOVIE_TITLE, currentMovie.getTitle());
        // Если у Movie есть ID:
        // resultIntent.putExtra(EXTRA_DELETED_MOVIE_ID, currentMovie.getId());

        setResult(Activity.RESULT_OK, resultIntent); // Устанавливаем результат OK
        finish(); // Закрываем текущую Activity
    }

    // Обработка ошибок загрузки данных
    private void handleDataError(String logMessage) {
        Log.e("FilmDetails", logMessage);
        Toast.makeText(this, "Ошибка загрузки данных фильма", Toast.LENGTH_LONG).show();
        // Завершаем активность, так как без данных она бесполезна
        finish();
    }
}