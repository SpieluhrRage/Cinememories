package com.example.platonov; // Свой пакет

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log; // Для логирования
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Для превью постера
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Убираем импорт Glide
// import com.bumptech.glide.Glide;

public class AddFilm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner genreSpinner;
    private ImageView posterPreviewImageView; // ImageView для превью
    private Button selectPosterButton;
    private Button saveButton;

    private String selectedGenre = null;
    private Uri selectedPosterUri = null; // Храним URI

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_film); // Убедись, что layout правильный

        // Инициализация View (убедись, что ID верные)
        titleEditText = findViewById(R.id.movieTitle);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        genreSpinner = findViewById(R.id.genreSpinner);
        posterPreviewImageView = findViewById(R.id.posterPreviewImageView); // Важный ImageView
        selectPosterButton = findViewById(R.id.selectPosterButton);
        saveButton = findViewById(R.id.saveButton);

        // Настройка Spinner (остается без изменений)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movie_genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedGenre = parent.getItemAtPosition(position).toString();
                } else {
                    selectedGenre = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGenre = null;
            }
        });

        // Кнопка выбора постера
        selectPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Кнопка сохранения
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovie();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Не найдено приложение галереи", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMovie() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedGenre == null) {
            Toast.makeText(this, "Выберите жанр", Toast.LENGTH_SHORT).show();
            return;
        }

        // Преобразуем URI в строку для сохранения (если URI был выбран)
        String posterUriString = (selectedPosterUri != null) ? selectedPosterUri.toString() : null;

        Movie newMovie = new Movie(title, description, selectedGenre, posterUriString);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("movie", newMovie);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // Обработка результата выбора изображения (БЕЗ GLIDE)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedPosterUri = data.getData(); // Сохраняем URI выбранного изображения

            // Отображаем выбранный постер в ImageView напрямую
            try {
                posterPreviewImageView.setImageURI(selectedPosterUri);
                // Опционально: Сделать ImageView видимым, если он был скрыт
                // posterPreviewImageView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                // Обработка возможных ошибок (например, OutOfMemoryError для больших картинок)
                Log.e("AddFilmActivity", "Error setting image URI to ImageView", e);
                Toast.makeText(this, "Не удалось отобразить превью постера", Toast.LENGTH_SHORT).show();
                // Можно установить заглушку или скрыть ImageView
                // posterPreviewImageView.setImageResource(R.drawable.placeholder_poster);
            }
        }
    }
}