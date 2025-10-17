package com.example.platonov.ui.detail;


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

import com.bumptech.glide.Glide;
import com.example.platonov.R;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.databinding.FragmentMovieDetailBinding;


public class MovieDetailFragment extends Fragment {

    private FragmentMovieDetailBinding binding;
    private MovieDetailViewModel viewModel;
    private long movieId;
    private MovieEntity currentMovie;

    public MovieDetailFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getLong("movieId", -1);
        }
        if (movieId < 0) {
            Toast.makeText(getContext(), "Ошибка: не передан ID фильма", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        viewModel.getMovieById(movieId).observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                currentMovie = movie;
                populateUI(movie);
            }
        });

        binding.buttonFavoriteDetail.setOnClickListener(v -> {
            if (currentMovie == null) return;
            boolean newFav = !currentMovie.isFavorite();
            currentMovie.setFavorite(newFav);
            viewModel.updateMovie(currentMovie);
            updateFavoriteIcon(newFav);
        });

        binding.ratingBarUser.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (!fromUser || currentMovie == null) return;
            currentMovie.setUserRating(rating);
            viewModel.updateMovie(currentMovie);
        });

        binding.buttonSaveNotes.setOnClickListener(v -> {
            if (currentMovie == null) return;
            String notes = binding.editTextDetailNotes.getText().toString().trim();
            currentMovie.setNotes(notes);
            viewModel.updateMovie(currentMovie);
            Toast.makeText(getContext(), "Заметки сохранены", Toast.LENGTH_SHORT).show();
        });
    }


    private void populateUI(@NonNull MovieEntity movie) {

        binding.textViewDetailTitle.setText(movie.getTitle());

        String yearGenre = movie.getYear() + ", " + movie.getGenre();
        binding.textViewDetailYearGenre.setText(yearGenre);

        if (!TextUtils.isEmpty(movie.getSynopsis())) {
            binding.textViewDetailSynopsis.setText(movie.getSynopsis());
        } else {
            binding.textViewDetailSynopsis.setText("Описание отсутствует");
        }

        String posterUrl = movie.getPosterUrl();
        if (!TextUtils.isEmpty(posterUrl)) {
            Glide.with(binding.imageViewDetailPoster.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.imageViewDetailPoster);
        } else {
            binding.imageViewDetailPoster.setImageResource(R.drawable.ic_placeholder);
        }

        updateFavoriteIcon(movie.isFavorite());
        binding.ratingBarUser.setRating(movie.getUserRating());


        binding.editTextDetailNotes.setText(movie.getNotes());
        binding.ratingBarUser.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                movie.setUserRating(rating);
                viewModel.updateMovie(movie);
                Toast.makeText(getContext(), "Рейтинг сохранён: " + rating, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            binding.buttonFavoriteDetail.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            binding.buttonFavoriteDetail.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}