package com.example.platonov.ui.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment; // <— ВАЖНО
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.platonov.R;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.databinding.FragmentMovieListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovieListFragment extends Fragment {

    private MovieListViewModel viewModel;
    private FragmentMovieListBinding binding;
    private MovieListAdapter adapter;

    public MovieListFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        adapter = new MovieListAdapter(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieEntity movie) {
                Bundle args = new Bundle();
                args.putLong("movieId", movie.getId());
                // было: Navigation.findNavController(view).navigate(...)
                NavHostFragment.findNavController(MovieListFragment.this)
                        .navigate(R.id.action_movieListFragment_to_movieDetailFragment, args);
            }

            @Override
            public void onDeleteClick(MovieEntity movie) {
                viewModel.deleteMovie(movie);
            }

            @Override
            public void onFavoriteClick(MovieEntity movie) {
                boolean newFav = !movie.isFavorite();
                movie.setFavorite(newFav);
                viewModel.updateMovie(movie);
            }
        });

        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewMovies.setAdapter(adapter);

        viewModel.getAllMovies().observe(getViewLifecycleOwner(), movies -> {
            adapter.submitList(movies);
        });

        // Если FAB находится в активности:
        FloatingActionButton fab = requireActivity().findViewById(R.id.fabAddMovie);
        fab.setOnClickListener(v -> {
            // было: Navigation.findNavController(view).navigate(...)
            NavHostFragment.findNavController(MovieListFragment.this)
                    .navigate(R.id.action_movieListFragment_to_addMovieFragment);
        });

        // Если когда-то перенесёшь FAB в layout этого фрагмента:
        // binding.fabAddMovie.setOnClickListener(v -> {
        //     NavHostFragment.findNavController(this)
        //         .navigate(R.id.action_movieListFragment_to_addMovieFragment);
        // });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
