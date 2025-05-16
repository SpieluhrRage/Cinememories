package com.example.platonov;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.platonov.db.DatabaseHelper;

import java.util.Arrays;

public class AddFilm extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    public static final String EXTRA_MOVIE_TO_EDIT = "movie_to_edit";
    public static final String EXTRA_SAVED_MOVIE = "saved_movie";


    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText releaseDateEditText;
    private Spinner genreSpinner;
    private ImageView posterPreviewImageView;
    private Button selectPosterButton;
    private Button saveButton;

    private String selectedGenre = null;
    private Uri selectedPosterUri = null;
    private Movie movieBeingEdited = null;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_film); // Убедитесь, что такой layout существует

        dbHelper = new DatabaseHelper(this);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        genreSpinner = findViewById(R.id.genreSpinner);
        posterPreviewImageView = findViewById(R.id.posterPreviewImageView);
        selectPosterButton = findViewById(R.id.selectPosterButton);
        saveButton = findViewById(R.id.saveButton);
        releaseDateEditText = findViewById(R.id.yearEditText);

        setupGenreSpinner();

        if (getIntent().hasExtra(EXTRA_MOVIE_TO_EDIT)) {
            movieBeingEdited = getIntent().getParcelableExtra(EXTRA_MOVIE_TO_EDIT);
            if (movieBeingEdited != null) {
                populateFieldsForEdit();
                setTitle("Редактировать фильм");
                releaseDateEditText.setText(movieBeingEdited.getReleaseDate());// Меняем заголовок Activity
            }
        } else {
            setTitle("Добавить новый фильм");
        }

        selectPosterButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveMovie());
    }

    private void setupGenreSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movie_genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Первый элемент ("Выберите жанр") не должен быть выбран как реальный жанр
                if (position > 0) {
                    selectedGenre = parent.getItemAtPosition(position).toString();
                } else {
                    selectedGenre = null; // Если выбран "Выберите жанр"
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGenre = null;
            }
        });
    }

    private void populateFieldsForEdit() {
        titleEditText.setText(movieBeingEdited.getTitle());
        descriptionEditText.setText(movieBeingEdited.getDescription());

        if (movieBeingEdited.getGenre() != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genreSpinner.getAdapter();
            // Находим позицию жанра в списке Spinner
            String[] genresArray = getResources().getStringArray(R.array.movie_genres);
            // Начинаем поиск с 1, так как 0 - это "Выберите жанр"
            for (int i = 1; i < genresArray.length; i++) {
                if (genresArray[i].equals(movieBeingEdited.getGenre())) {
                    genreSpinner.setSelection(i);
                    selectedGenre = movieBeingEdited.getGenre(); // Устанавливаем выбранный жанр
                    break;
                }
            }
        }

        if (movieBeingEdited.getPosterUri() != null) {
            selectedPosterUri = Uri.parse(movieBeingEdited.getPosterUri());
            posterPreviewImageView.setImageURI(selectedPosterUri);
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Для API 19+ можно также добавить Intent.ACTION_OPEN_DOCUMENT
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Не найдено приложение галереи", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMovie() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String releaseDate = releaseDateEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedGenre == null || selectedGenre.equals(getResources().getStringArray(R.array.movie_genres)[0])) { // Проверяем, не выбран ли плейсхолдер
            Toast.makeText(this, "Выберите жанр", Toast.LENGTH_SHORT).show();
            return;
        }

        String posterUriString = (selectedPosterUri != null) ? selectedPosterUri.toString() : null;

        Intent resultIntent = new Intent();
        boolean success = false;

        if (movieBeingEdited != null) { // Режим редактирования
            movieBeingEdited.setTitle(title);
            movieBeingEdited.setDescription(description);
            movieBeingEdited.setGenre(selectedGenre);
            movieBeingEdited.setPosterUri(posterUriString);
            movieBeingEdited.setReleaseDate(releaseDate);

            int updatedRows = dbHelper.updateMovie(movieBeingEdited);
            if (updatedRows > 0) {
                Toast.makeText(this, "Фильм обновлен", Toast.LENGTH_SHORT).show();
                resultIntent.putExtra(EXTRA_SAVED_MOVIE, movieBeingEdited);
                success = true;
            } else {
                Toast.makeText(this, "Ошибка обновления фильма", Toast.LENGTH_SHORT).show();
            }
        } else { // Режим добавления
            Movie newMovie = new Movie(title, description, selectedGenre, posterUriString, releaseDate, null);
            long newId = dbHelper.addMovie(newMovie);
            if (newId != -1) {
                newMovie.setId(newId); // Устанавливаем ID, полученный от БД
                Toast.makeText(this, "Фильм добавлен", Toast.LENGTH_SHORT).show();
                resultIntent.putExtra(EXTRA_SAVED_MOVIE, newMovie);
                success = true;
            } else {
                Toast.makeText(this, "Ошибка добавления фильма", Toast.LENGTH_SHORT).show();
            }
        }

        if (success) {
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedPosterUri = data.getData();
            // Важно: для постоянного доступа к файлу после перезагрузки устройства,
            // нужно скопировать файл в приватное хранилище приложения или получить постоянные права.
            // Для простоты сейчас просто отображаем.
            // Для API 19+ (KitKat) и выше, URI может требовать дополнительных разрешений
            // getContentResolver().takePersistableUriPermission(selectedPosterUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                posterPreviewImageView.setImageURI(selectedPosterUri);
            } catch (Exception e) {
                Log.e("AddFilm", "Error setting image URI", e);
                Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
            }
        }
    }
}