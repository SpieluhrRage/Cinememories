package com.example.platonov.fragments;

import android.app.Activity; // Для onActivityResult
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.platonov.AddFilm; // Импорт Activity добавления
import com.example.platonov.FilmDetails;
import com.example.platonov.Movie;
import com.example.platonov.MovieAdapter;
import com.example.platonov.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Для FAB

import java.util.ArrayList;

public class AllMoviesFragment extends Fragment {

    private static final int ADD_MOVIE_REQUEST_FRAGMENT = 2; // Другой код запроса
    private ArrayList<Movie> movieList = new ArrayList<>(); // Список фильмов для этого фрагмента
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private static final int VIEW_MOVIE_DETAILS_REQUEST = 1001;
    private static boolean initialDataAddedFragment = false; // Отдельный флаг для фрагмента

    public AllMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_movies, container, false);

        recyclerView = view.findViewById(R.id.movieRecyclerView);
        FloatingActionButton fabAddMovie = view.findViewById(R.id.fabAddMovie); // Находим FAB

        // Предзаполнение (если еще не было или список пуст после восстановления)
        if (movieList.isEmpty()) { // Проверяем, пуст ли список
            // TODO: Здесь лучше загружать данные из БД/хранилища
            // Если загрузка не дала результатов И данные еще не добавлялись:
            if (!initialDataAddedFragment) {
                addInitialMovies();
                initialDataAddedFragment = true;
            }
        }

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Передаем getContext() вместо this
        adapter = new MovieAdapter(getContext(), movieList);
        recyclerView.setAdapter(adapter);

        // Обработчик для FAB
        fabAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFilm.class);
            // Используем startActivityForResult из фрагмента
            startActivityForResult(intent, ADD_MOVIE_REQUEST_FRAGMENT);
        });

        return view;
    }

    // Метод предзаполнения (можно вынести в общий класс данных)
    private void addInitialMovies() {
        movieList.add(new Movie("Начало", "...", "Фантастика", getUriStringForDrawable(R.drawable.poster)));
        movieList.add(new Movie("Интерстеллар", "...", "НФ", getUriStringForDrawable(R.drawable.poster)));
        movieList.add(new Movie("Темный рыцарь", "...", "Боевик", getUriStringForDrawable(R.drawable.poster)));
        // TODO: Сохранить в БД
    }

    // Вспомогательный метод для URI drawable (нужен Context)
    private String getUriStringForDrawable(int resourceId) {
        if (getContext() == null) return null;
        try {
            Resources res = getContext().getResources();
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resourceId)
                    + '/' + res.getResourceTypeName(resourceId)
                    + '/' + res.getResourceEntryName(resourceId)).toString();
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    // Обработка результата от AddFilmActivity во фрагменте
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("AllMoviesFragment", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode); // Лог для отладки

        if (requestCode == VIEW_MOVIE_DETAILS_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Проверяем, был ли фильм удален (используем ключ из FilmDetails)
            if (data.hasExtra(FilmDetails.EXTRA_DELETED_MOVIE_TITLE)) {
                String deletedTitle = data.getStringExtra(FilmDetails.EXTRA_DELETED_MOVIE_TITLE);
                Log.d("AllMoviesFragment", "Received deleted movie title: " + deletedTitle); // Лог

                if (deletedTitle != null && movieList != null && adapter != null) {
                    // Находим и удаляем фильм из списка
                    Movie movieToRemove = null;
                    int positionToRemove = -1;
                    for (int i = 0; i < movieList.size(); i++) {
                        Movie movie = movieList.get(i);
                        if (movie != null && deletedTitle.equals(movie.getTitle())) {
                            movieToRemove = movie;
                            positionToRemove = i;
                            break;
                        }
                    }

                    if (movieToRemove != null && positionToRemove != -1) {
                        movieList.remove(positionToRemove);
                        // Уведомляем адаптер об удалении конкретного элемента
                        adapter.notifyItemRemoved(positionToRemove);
                        // Опционально: обновить диапазон, если порядок важен
                        adapter.notifyItemRangeChanged(positionToRemove, movieList.size());
                        Log.d("AllMoviesFragment", "Movie removed from list and adapter notified."); // Лог
                        // TODO: Убедись, что фильм также удален из постоянного хранилища!
                    } else {
                        Log.w("AllMoviesFragment", "Movie with title '" + deletedTitle + "' not found in the list."); // Лог
                    }
                }
            }
        }
    }
    // TODO: Добавить сохранение/восстановление movieList в onSaveInstanceState
}