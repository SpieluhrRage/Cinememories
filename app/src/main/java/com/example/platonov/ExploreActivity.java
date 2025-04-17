package com.example.platonov; // Свой пакет

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar; // Для ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.platonov.fragments.explore.GenresFragment; // Положи в подпакет explore
import com.example.platonov.fragments.explore.SearchFragment;
import com.example.platonov.fragments.explore.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExploreActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActionBar actionBar; // Для установки заголовка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        Toolbar toolbar = findViewById(R.id.explore_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // Добавляем кнопку "Назад" в ActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Загружаем фрагмент по умолчанию
        if (savedInstanceState == null) {
            loadFragment(new GenresFragment());
            if (actionBar != null) actionBar.setTitle("Жанры"); // Устанавливаем заголовок
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = getString(R.string.app_name); // Заголовок по умолчанию
        int id = item.getItemId();

        if (id == R.id.bottom_nav_genres) {
            selectedFragment = new GenresFragment();
            title = "Жанры";
        } else if (id == R.id.bottom_nav_trending) {
            selectedFragment = new TrendingFragment();
            title = "Популярное";
        } else if (id == R.id.bottom_nav_search) {
            selectedFragment = new SearchFragment();
            title = "Поиск";
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            if (actionBar != null) {
                actionBar.setTitle(title); // Обновляем заголовок ActionBar
            }
            return true; // Успешно обработано
        }
        return false; // Не обработано
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.explore_fragment_container, fragment)
                .commit();
    }

    // Обработка нажатия на кнопку "Назад" в ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обрабатываем нажатие на системную кнопку "назад" (стрелка)
        if (item.getItemId() == android.R.id.home) {
            finish(); // Закрываем текущую активность и возвращаемся к предыдущей
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}