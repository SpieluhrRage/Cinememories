package com.example.platonov;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddFilm extends AppCompatActivity {
    private EditText titleEditText, yearEditText, genreEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_film);

        titleEditText = findViewById(R.id.movieTitle);
        yearEditText = findViewById(R.id.movieYear);
        genreEditText = findViewById(R.id.movieGenre);

        Button saveButton = findViewById(R.id.saveMovieButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String year = yearEditText.getText().toString();
                String genre = genreEditText.getText().toString();

                Movie newMovie = new Movie(title, year, genre);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("movie", newMovie);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(AddFilm.this, MainScreen.class);
                back.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(back);

            }
        });
    }
}