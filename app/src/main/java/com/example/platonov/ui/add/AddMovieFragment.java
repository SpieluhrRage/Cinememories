package com.example.platonov.ui.add;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.platonov.databinding.FragmentAddMovieBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Фрагмент с ViewPager2 и TabLayout: две вкладки —
 *  1) Manual
 *  2) Search TMDb
 */
public class AddMovieFragment extends Fragment {

    private FragmentAddMovieBinding binding;

    public AddMovieFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Настраиваем ViewPager2 с нашим адаптером
        AddMoviePagerAdapter pagerAdapter = new AddMoviePagerAdapter(getActivity());
        binding.viewPagerAdd.setAdapter(pagerAdapter);

        // Синхронизируем TabLayout с ViewPager2
        new TabLayoutMediator(binding.tabLayoutAdd, binding.viewPagerAdd,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Manual");
                    } else {
                        tab.setText("Search");
                    }
                }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}