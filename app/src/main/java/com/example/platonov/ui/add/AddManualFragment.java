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

import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.databinding.FragmentAddManualBinding;
import com.example.platonov.ui.collection.MovieListViewModel;

/**
 * Фрагмент для ручного добавления фильма.
 */
public class AddManualFragment extends Fragment {

    private FragmentAddManualBinding binding;
    private MovieListViewModel viewModel;

    public AddManualFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddManualBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем тот же ViewModel, что и в MovieListFragment,
        // чтобы при добавлении новый фильм сразу появился в списке.
        viewModel = new ViewModelProvider(requireActivity()).get(MovieListViewModel.class);

        binding.buttonManualSave.setOnClickListener(v -> {
            String title = binding.editTextManualTitle.getText().toString().trim();
            String yearStr = binding.editTextManualYear.getText().toString().trim();
            String genre = binding.editTextManualGenre.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                binding.editTextManualTitle.setError("Введите название");
                return;
            }
            if (TextUtils.isEmpty(yearStr)) {
                binding.editTextManualYear.setError("Введите год");
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                binding.editTextManualYear.setError("Некорректный год");
                return;
            }
            if (TextUtils.isEmpty(genre)) {
                genre = ""; // допустим, оставляем пустым
            }

            // Создаём MovieEntity с минимальным набором полей
            MovieEntity movie = new MovieEntity(
                    title,
                    year,
                    genre,
                    null,     // posterUrl = null (нет постера)
                    "",       // synopsis пустой
                    false,    // isFavorite

                    0f,       // userRating
                    ""        // notes
            );
            // Сохраняем в базу
            viewModel.insertMovie(movie);
            Toast.makeText(getContext(), "Фильм добавлен", Toast.LENGTH_SHORT).show();

            // Очистим поля
            binding.editTextManualTitle.setText("");
            binding.editTextManualYear.setText("");
            binding.editTextManualGenre.setText("");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}