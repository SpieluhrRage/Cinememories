package com.example.platonov; // Свой пакет

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources; // Для доступа к ресурсам
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Если нужна строка поиска
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu; // <-- Импорт для меню
import android.view.MenuInflater; // <-- Импорт для инфлейтера меню
import android.view.MenuItem; // <-- Импорт для элемента меню
import androidx.annotation.NonNull; // <-- Импорт для @NonNull
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List; // Используем List

public class MainScreen extends AppCompatActivity {

    private static final int ADD_MOVIE_REQUEST = 1;
    private ArrayList<Movie> movieList; // Наш основной список фильмов
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchField; // Поле поиска

    // Флаг, чтобы не добавлять стартовые данные каждый раз при повороте и т.п.
    // Лучше использовать ViewModel или сохранение состояния, но для простоты пока так.
    private static boolean initialDataAdded = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // Надуваем наше меню из XML
        return true; // Меню создано
    }

    // Метод для обработки нажатий на элементы меню
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Получаем ID нажатого элемента

        if (id == R.id.action_categories) {
            // Переход на экран категорий
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);
            return true; // Событие обработано
        } else if (id == R.id.action_about) {
            // Переход на экран "О приложении"
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true; // Событие обработано
        }

        // Если не обработали мы, передаем дальше
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen); // Убедись, что ID RecyclerView в XML = movieRecyclerView
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar); // Находим Toolbar по ID
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.movieRecyclerView);
        searchField = findViewById(R.id.searchField); // Найдем поле поиска
        Button addMovieButton = findViewById(R.id.addMovieButton);

        movieList = new ArrayList<>();

        // Предзаполнение списка (только один раз)
        if (savedInstanceState == null && !initialDataAdded) { // Проверяем savedInstanceState тоже
            addInitialMovies();
            initialDataAdded = true;
        }
        // TODO: Здесь должна быть логика загрузки сохраненных фильмов из БД/файла,
        // если initialDataAdded == true или movieList пуст после загрузки.

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, movieList);
        recyclerView.setAdapter(adapter);

        // Кнопка добавления
        addMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, AddFilm.class); // Переход на твою Activity добавления
                startActivityForResult(intent, ADD_MOVIE_REQUEST);
            }
        });

        // TODO: Добавить TextWatcher для searchField для фильтрации списка adapter.getFilter().filter(text);
    }

    // Метод предзаполнения
    private void addInitialMovies() {
        // Добавь файлы постеров в res/drawable (например, poster_inception.jpg)
        movieList.add(new Movie("Начало", "Группа воров внедряется в сны...", "Фантастика", getUriStringForDrawable(R.drawable.poster)));
        movieList.add(new Movie("Интерстеллар", "Путешествие к другим галактикам...", "Научная фантастика", getUriStringForDrawable(R.drawable.poster)));
        movieList.add(new Movie("Темный рыцарь", "Бэтмен против Джокера...", "Боевик", getUriStringForDrawable(R.drawable.poster)));
        // Добавь еще...

        // Важно: если используешь базу данных, сохрани эти фильмы и туда.
    }

    // Вспомогательный метод для получения URI ресурса drawable
    private String getUriStringForDrawable(int resourceId) {
        try {
            // Проверяем существование ресурса
            Resources res = getResources();
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resourceId)
                    + '/' + res.getResourceTypeName(resourceId)
                    + '/' + res.getResourceEntryName(resourceId)).toString();
        } catch (Resources.NotFoundException e) {
            return null; // Возвращаем null, если ресурс не найден
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MOVIE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Убедись, что AddFilmActivity возвращает Movie с ключом "movie"
            Movie newMovie = data.getParcelableExtra("movie");
            if (newMovie != null) {
                adapter.addMovie(newMovie); // Добавляем через метод адаптера
                recyclerView.scrollToPosition(adapter.getItemCount() - 1); // Прокрутка к добавленному
                // TODO: Сохранить newMovie в постоянное хранилище (БД/файл)
            }
        }
    }

}