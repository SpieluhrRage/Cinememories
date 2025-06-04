package com.example.platonov.ui.search;


import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.platonov.databinding.FragmentSearchFilterBinding;
import com.example.platonov.ui.collection.MovieListAdapter;

import java.util.List;

/**
 * Фрагмент для экрана «Поиск/Фильтрация».
 */
public class SearchFilterFragment extends Fragment {

    private FragmentSearchFilterBinding binding;
    private SearchFilterViewModel viewModel;
    private MovieListAdapter adapter;

    public SearchFilterFragment() {
        // Пустой конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) ViewModel
        viewModel = new ViewModelProvider(this).get(SearchFilterViewModel.class);

        // 2) Настраиваем RecyclerView
        adapter = new MovieListAdapter(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieEntity movie) {
                // Навигация к деталям
                Bundle args = new Bundle();
                args.putLong("movieId", movie.getId());
                Navigation.findNavController(view)
                        .navigate(R.id.action_searchFilterFragment_to_movieDetailFragment, args);
            }

            @Override
            public void onFavoriteClick(MovieEntity movie) {
                // Меняем фильм на избранный
                boolean newFav = !movie.isFavorite();
                movie.setFavorite(newFav);
                viewModel.getFilteredMovies(null, null, null, null)
                        .getValue() // получаем текущий список (если нужно)
                ; // но лучше поднять обновление через общий репозиторий
            }

            @Override
            public void onDeleteClick(MovieEntity movie) {

            }
        });

        binding.recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSearchResults.setAdapter(adapter);

        // 3) Обработка клика на «Apply Filter»
        binding.buttonApplyFilter.setOnClickListener(v -> {
            String genreInput = binding.editTextGenre.getText().toString().trim();
            String yearFromStr = binding.editTextYearFrom.getText().toString().trim();
            String yearToStr = binding.editTextYearTo.getText().toString().trim();
            float minRating = binding.ratingBarFilter.getRating();

            // Преобразуем строки в Integer (если не пусто)
            Integer yearFrom = null;
            Integer yearTo = null;
            if (!TextUtils.isEmpty(yearFromStr)) {
                try {
                    yearFrom = Integer.parseInt(yearFromStr);
                } catch (NumberFormatException e) {
                    yearFrom = null;
                }
            }
            if (!TextUtils.isEmpty(yearToStr)) {
                try {
                    yearTo = Integer.parseInt(yearToStr);
                } catch (NumberFormatException e) {
                    yearTo = null;
                }
            }
            // Если rating = 0, считаем, что фильтрация по рейтингу не нужна
            Float ratingFilter = minRating > 0 ? minRating : null;

            // Вызываем ViewModel для получения отфильтрованного списка
            viewModel.getFilteredMovies(
                    genreInput.isEmpty() ? null : genreInput,
                    yearFrom,
                    yearTo,
                    ratingFilter
            ).observe(getViewLifecycleOwner(), movies -> {
                adapter.submitList(movies);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
