package com.example.platonov;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.platonov.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Инициализируем ViewBinding для activity_main.xml
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Получаем NavHostFragment по его ID (должен совпадать с ID из XML)
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            // Если navHostFragment == null, значит у вас неверный ID в разметке
            throw new IllegalStateException("NavHostFragment not found. Check your activity_main.xml");
        }

        // 3. Получаем NavController из NavHostFragment
        navController = navHostFragment.getNavController();

        // 4. Привязываем BottomNavigationView к NavController
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // (Необязательно) Можно менять заголовок Activity при смене вкладок
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            setTitle(destination.getLabel());
        });

        // (Если вы используете FloatingActionButton именно в activity_main.xml)
        binding.fabAddMovie.setOnClickListener(v -> {
            // уже на Add — ничего не делаем
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.addMovieFragment) {
                return;
            }

            // если экран Add уже есть в back stack — просто вернёмся к нему
            boolean popped = navController.popBackStack(R.id.addMovieFragment, false);

            if (!popped) {
                // иначе откроем его (и защитимся от дабл-кликов)
                v.setEnabled(false);
                navController.navigate(R.id.addMovieFragment);
                v.postDelayed(() -> v.setEnabled(true), 400);
            }
        });




    }
}
