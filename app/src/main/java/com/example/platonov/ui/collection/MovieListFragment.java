package com.example.platonov.ui.collection;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.platonov.R;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.databinding.FragmentMovieListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Фрагмент для отображения списка фильмов (A-1).
 */
public class MovieListFragment extends Fragment {

    private MovieListViewModel viewModel;
    private FragmentMovieListBinding binding;
    private MovieListAdapter adapter;

    public MovieListFragment() {
        // Обязательный пустой конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Используем ViewBinding для inflated макета fragment_movie_list.xml
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализируем ViewModel
        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        // Настраиваем RecyclerView
        adapter = new MovieListAdapter(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieEntity movie) {
                // При клике на элемент списка переходим к детальному экрану
                // Предполагается, что в навигационном графе есть action_movieListFragment_to_movieDetailFragment
                Bundle args = new Bundle();
                args.putLong("movieId", movie.getId());
                Navigation.findNavController(view)
                        .navigate(R.id.action_movieListFragment_to_movieDetailFragment, args);
            }
            @Override
            public void onDeleteClick(MovieEntity movie) {
                // Удаляем фильм через ViewModel
                viewModel.deleteMovie(movie);
            }

            @Override
            public void onFavoriteClick(MovieEntity movie) {
                // При клике на "звездочку" меняем флаг isFavorite
                boolean newFav = !movie.isFavorite();
                movie.setFavorite(newFav);
                viewModel.updateMovie(movie);
                // LiveData автоматически обновит список
            }
        });

        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMovies.setAdapter(adapter);

        // Наблюдаем за списком фильмов и передаём данные адаптеру


        // 2) Подписка на LiveData getAllMovies()
        viewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            adapter.submitList(movies);
        });
        FloatingActionButton fab = requireActivity().findViewById(R.id.fabAddMovie);
        fab.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_movieListFragment_to_addMovieFragment);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Обнуляем binding, чтобы избежать утечек памяти
        binding = null;
    }
}