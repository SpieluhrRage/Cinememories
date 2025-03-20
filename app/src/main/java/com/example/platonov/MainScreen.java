package com.example.platonov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    private ArrayList<Movie> movieList;
    private ArrayAdapter<Movie> adapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        movieList = new ArrayList<>();
        listView = findViewById(R.id.movieList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);

        Button addMovieButton = findViewById(R.id.addMovieButton);
        addMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, AddFilm.class);
                startActivityForResult(intent, 1); // Запуск активности с ожиданием результата
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Получаем объект Movie из Intent
            Movie newMovie = data.getParcelableExtra("movie");

            if (newMovie != null) {
                movieList.add(newMovie); // Добавляем фильм в список
                adapter.notifyDataSetChanged(); // Обновляем ListView
            }
        }
    }
}