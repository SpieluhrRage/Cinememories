package com.example.platonov;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FilmDetails extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView genreTextView; // Если хочешь показать жанр
    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_film_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        titleTextView = findViewById(R.id.movieTitle); // Замени на свои ID
        descriptionTextView = findViewById(R.id.movieDescription); // Замени на свои ID
        genreTextView = findViewById(R.id.movieGenre); // Замени на свои ID (если есть)
        posterImageView = findViewById(R.id.moviePoster);
        Intent intent = getIntent();

        // Проверяем, что Intent существует и содержит нужные данные
        if (intent != null && intent.hasExtra("movie")) {
            // Извлекаем объект Movie (он должен быть Parcelable)
            Movie movie = intent.getParcelableExtra("movie");

            // Проверяем, что объект не null
            if (movie != null) {
                // Заполняем View данными из объекта Movie
                titleTextView.setText(movie.getTitle());
                descriptionTextView.setText(movie.getDescription());

                // Устанавливаем жанр (если поле есть)
                if (genreTextView != null && movie.getGenre() != null) {
                    genreTextView.setText("Жанр: " + movie.getGenre());
                } else if (genreTextView != null) {
                    genreTextView.setText("Жанр: Не указан");
                }

                // Загружаем постер
                String posterUriString = movie.getPosterUri();
                if (posterUriString != null && !posterUriString.isEmpty()) {
                    try {
                        Uri posterUri = Uri.parse(posterUriString);
                        posterImageView.setImageURI(posterUri);
                    } catch (Exception e) {
                        Log.e("MovieDetailActivity", "Error loading poster URI: " + posterUriString, e);
                        posterImageView.setImageResource(R.drawable.poster); // Заглушка при ошибке
                    }
                } else {
                    posterImageView.setImageResource(R.drawable.poster); // Заглушка, если URI нет
                }
            } else {
                // Ошибка: объект Movie == null
                Log.e("MovieDetailActivity", "Movie object received from Intent is null");
                Toast.makeText(this, "Ошибка: не удалось загрузить данные фильма", Toast.LENGTH_SHORT).show();
                // Можно показать сообщение об ошибке или закрыть Activity
                // finish();
            }
        } else {
            // Ошибка: Intent не содержит ключ "movie"
            Log.e("MovieDetailActivity", "Intent is null or does not contain 'movie' extra");
            Toast.makeText(this, "Ошибка: не переданы данные о фильме", Toast.LENGTH_SHORT).show();
            // Можно показать сообщение об ошибке или закрыть Activity
            // finish();
        }
    }
}