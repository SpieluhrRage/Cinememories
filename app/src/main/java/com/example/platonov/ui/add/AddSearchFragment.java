package com.example.platonov.ui.add;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.databinding.FragmentAddSearchBinding;
import com.example.platonov.ui.collection.MovieListAdapter;

import java.util.List;

/**
 * Фрагмент для поиска фильма в TMDb и добавления выбранного результата в локальную БД.
 */
public class AddSearchFragment extends Fragment {

    private FragmentAddSearchBinding binding;
    private AddMovieViewModel addMovieViewModel;
    private MovieListAdapter adapter;

    public AddSearchFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем общий ViewModel для добавления (он будет обращаться к репозиторию TMDb)
        addMovieViewModel = new ViewModelProvider(requireActivity()).get(AddMovieViewModel.class);

        // Настраиваем RecyclerView для результатов поиска
        adapter = new MovieListAdapter(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieEntity movie) {
                // При клике на результат TMDb сразу сохраняем его в локальную БД:
                addMovieViewModel.insertMovie(movie);
                Toast.makeText(getContext(), "Добавлено: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteClick(MovieEntity movie) {
                // У TMDb-шаблона обычно isFavorite=false, но если захотите сразу пометить —
                // movie.setFavorite(true);
                // addMovieViewModel.updateMovie(movie);
            }
            @Override
            public void onDeleteClick(MovieEntity movie) {
                // Удаляем фильм через ViewModel

            }
        });

        binding.recyclerViewTmdbResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTmdbResults.setAdapter(adapter);

        // Нажатие на кнопку Search
        binding.buttonSearchTmdb.setOnClickListener(v -> {
            String query = binding.editTextSearchQuery.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                binding.editTextSearchQuery.setError("Введите название фильма");
                return;
            }
            // Вызываем метод ViewModel, который вернёт LiveData<List<MovieEntity>> из TMDb
            addMovieViewModel.searchMovies(query).observe(getViewLifecycleOwner(), tmdbMovies -> {
                // По приходу списка обновляем адаптер
                adapter.submitList(tmdbMovies);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
