package com.example.platonov.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.platonov.AddFilm;
import com.example.platonov.FilmDetails;
import com.example.platonov.Movie;
import com.example.platonov.MovieAdapter;
import com.example.platonov.R;
import com.example.platonov.db.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AllMoviesFragment extends Fragment {

    private static final int ADD_MOVIE_REQUEST = 2; // Для добавления нового
    private static final int VIEW_MOVIE_DETAILS_REQUEST = 1; // Для просмотра/редактирования/удаления

    private ArrayList<Movie> movieList;
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    public AllMoviesFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
        movieList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_all_movies, container, false);

            recyclerView = view.findViewById(R.id.movieRecyclerView);
            FloatingActionButton fabAddMovie = view.findViewById(R.id.fabAddMovie);
            EditText searchEditText = view.findViewById(R.id.searchEditText);
            Button searchButton = view.findViewById(R.id.searchButton);

            // Убедимся, что movieList инициализирован (хотя вы это делаете в onCreate)
            if (movieList == null) {
                movieList = new ArrayList<>();
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Создаем адаптер здесь, ПОСЛЕ инициализации recyclerView
            adapter = new MovieAdapter(requireContext(), movieList, movie -> {
                Intent intent = new Intent(getActivity(), FilmDetails.class);
                intent.putExtra("movie", movie);
                startActivityForResult(intent, VIEW_MOVIE_DETAILS_REQUEST);
            });
            recyclerView.setAdapter(adapter); // Теперь это должно работать

            loadMoviesFromDatabase(); // Загружаем данные, это обновит адаптер через notifyDataSetChanged()

            fabAddMovie.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddFilm.class);
                startActivityForResult(intent, ADD_MOVIE_REQUEST);
            });

            searchButton.setOnClickListener(v -> {
                String searchTerm = searchEditText.getText().toString().trim();
                if (!searchTerm.isEmpty()) {
                    Movie foundMovie = dbHelper.findMovieByTitle(searchTerm);
                    if (foundMovie != null) {
                        Intent intent = new Intent(getActivity(), FilmDetails.class);
                        intent.putExtra("movie", foundMovie);
                        startActivityForResult(intent, VIEW_MOVIE_DETAILS_REQUEST);
                    } else {
                        Toast.makeText(getContext(), "Фильм '" + searchTerm + "' не найден", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadMoviesFromDatabase();
                }
            });

            return view;
    }

    private void loadMoviesFromDatabase() {
        Log.d("AllMoviesFragment", "loadMoviesFromDatabase: loading movies...");
        List<Movie> moviesFromDb = dbHelper.getAllMovies();
        Log.d("AllMoviesFragment", "loadMoviesFromDatabase: found " + moviesFromDb.size() + " movies in DB.");
        movieList.clear();
        movieList.addAll(moviesFromDb);
        if (adapter != null) {
            Log.d("AllMoviesFragment", "loadMoviesFromDatabase: notifying adapter.");
            adapter.updateMovies(moviesFromDb);
        } else {
            Log.e("AllMoviesFragment", "loadMoviesFromDatabase: ADAPTER IS NULL!");
        }

        if (moviesFromDb.isEmpty() && !wasInitialDataAdded()) {
            addInitialMoviesToDatabase();
            moviesFromDb = dbHelper.getAllMovies();
            movieList.clear();
            movieList.addAll(moviesFromDb);
            if (adapter != null) adapter.notifyDataSetChanged();
            markInitialDataAsAdded();
        }
    }
    private void addInitialMoviesToDatabase() {
        // Проверяем, есть ли они уже, чтобы не дублировать при каждом пустом списке
        if(dbHelper.findMovieByTitle("Начало") == null)
            dbHelper.addMovie(new Movie("Начало", "Сны во сне", "Фантастика", getUriStringForDrawable(R.drawable.poster)));
        if(dbHelper.findMovieByTitle("Интерстеллар") == null)
            dbHelper.addMovie(new Movie("Интерстеллар", "Путешествия", "НФ", getUriStringForDrawable(R.drawable.poster)));
        if(dbHelper.findMovieByTitle("Темный рыцарь") == null)
            dbHelper.addMovie(new Movie("Темный рыцарь", "Бэтмен", "Боевик", getUriStringForDrawable(R.drawable.poster)));
        Log.i("AllMoviesFragment", "Initial movies checked/added to database.");
    }


    private boolean wasInitialDataAdded() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("initial_data_added_v2", false); // Изменим ключ, чтобы сработало один раз
    }

    private void markInitialDataAsAdded() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("initial_data_added_v2", true).apply();
    }

    private String getUriStringForDrawable(int resourceId) {
        if (getContext() == null) return null;
        try {
            Resources res = getContext().getResources();
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resourceId)
                    + '/' + res.getResourceTypeName(resourceId)
                    + '/' + res.getResourceEntryName(resourceId)).toString();
        } catch (Resources.NotFoundException e) {
            Log.e("AllMoviesFragment", "Drawable resource not found: " + resourceId, e);
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AllMoviesFragment", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == ADD_MOVIE_REQUEST) {
                Movie savedMovie = data.getParcelableExtra(AddFilm.EXTRA_SAVED_MOVIE);
                if (savedMovie != null) {
                    Log.d("AllMoviesFragment", "onActivityResult: ADD_MOVIE_REQUEST - Movie saved: " + savedMovie.getTitle() + " with ID: " + savedMovie.getId());
                    loadMoviesFromDatabase();
                    Toast.makeText(getContext(), "Фильм \"" + savedMovie.getTitle() + "\" добавлен", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == VIEW_MOVIE_DETAILS_REQUEST) { // Результат от FilmDetails
                if (data.hasExtra(FilmDetails.EXTRA_DELETED_MOVIE_ID)) {
                    long deletedMovieId = data.getLongExtra(FilmDetails.EXTRA_DELETED_MOVIE_ID, -1);
                    if (deletedMovieId != -1) {
                        // Фильм уже удален из БД в FilmDetails, просто обновляем список
                        loadMoviesFromDatabase();
                        Toast.makeText(getContext(), "Фильм удален, список обновлен", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.hasExtra(FilmDetails.EXTRA_UPDATED_MOVIE_ID)) {
                    long updatedMovieId = data.getLongExtra(FilmDetails.EXTRA_UPDATED_MOVIE_ID, -1);
                    if (updatedMovieId != -1) {
                        // Фильм уже обновлен в БД через FilmDetails -> AddFilm, просто обновляем список
                        loadMoviesFromDatabase();
                        Toast.makeText(getContext(), "Фильм изменен, список обновлен", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}