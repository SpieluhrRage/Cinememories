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
import com.example.platonov.databinding.FragmentFavoritesBinding;

import java.util.List;

/**
 * Фрагмент для экрана «Избранное» (A-3 часть).
 */
public class FavoritesFragment extends Fragment {

    private FavoritesViewModel viewModel;
    private FragmentFavoritesBinding binding;
    private MovieListAdapter adapter; // Повторно используем адаптер из collection

    public FavoritesFragment() {
        // Пустой конструктор обязательно нужен
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализируем ViewModel
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Настраиваем RecyclerView, переиспользуем MovieListAdapter
        adapter = new MovieListAdapter(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieEntity movie) {
                // При клике переходим к деталям (тот же action, как в списке)
                Bundle args = new Bundle();
                args.putLong("movieId", movie.getId());
                Navigation.findNavController(view)
                        .navigate(R.id.action_favoritesFragment_to_movieDetailFragment, args);
            }

            @Override
            public void onFavoriteClick(MovieEntity movie) {
                // Если пользователь убрал «звездочку», снимаем флаг isFavorite
                boolean newFav = false;
                movie.setFavorite(newFav);
                viewModel.updateMovie(movie);
                // LiveData возобновит список и уберёт этот элемент из экрана
            }

            @Override
            public void onDeleteClick(MovieEntity movie) {

            }
        });

        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewFavorites.setAdapter(adapter);

        // Наблюдаем за списком избранного и обновляем адаптер
        viewModel.getFavoriteMovies().observe(getViewLifecycleOwner(), movies -> {
            adapter.submitList(movies);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}