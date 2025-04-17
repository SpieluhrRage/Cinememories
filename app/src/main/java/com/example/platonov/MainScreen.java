package com.example.platonov; // Свой пакет

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem; // Для MenuItem
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Используем Toolbar
import androidx.core.view.GravityCompat; // Для закрытия Drawer
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment; // Для Фрагментов

import com.example.platonov.fragments.AboutFragment;
import com.example.platonov.fragments.AllMoviesFragment;
import com.example.platonov.fragments.WishlistFragment;
import com.google.android.material.navigation.NavigationView;

// Реализуем интерфейс слушателя для NavigationView
public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle; // Убрали public
    private NavigationView navigationView;
    private Toolbar toolbar; // Добавили Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Устанавливаем Toolbar как ActionBar

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // --- Настройка ActionBarDrawerToggle ---
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, // Передаем toolbar
                R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Синхронизируем состояние (показывает иконку-гамбургер)

        // Устанавливаем слушатель для NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        // Загружаем начальный фрагмент (если не восстанавливаем состояние)
        if (savedInstanceState == null) {
            loadFragment(new AllMoviesFragment());
            navigationView.setCheckedItem(R.id.nav_all_movies); // Выделяем первый пункт
        }
    }

    // Обработка нажатий на элементы NavigationView
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_all_movies) {
            selectedFragment = new AllMoviesFragment();
        } else if (id == R.id.nav_explore) {
            // Переход на вторую Activity (Задание 2)
            Intent intent = new Intent(MainScreen.this, ExploreActivity.class); // Убедись, что ExploreActivity создана
            startActivity(intent);
            // Не меняем фрагмент, просто закрываем drawer
        } else if (id == R.id.nav_wishlist) {
            selectedFragment = new WishlistFragment();
        } else if (id == R.id.nav_about) {
            selectedFragment = new AboutFragment();
        }

        // Загружаем выбранный фрагмент, если он не null
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        // Закрываем DrawerLayout
        drawerLayout.closeDrawer(GravityCompat.START);
        return true; // Возвращаем true, так как обработали нажатие
    }

    // Метод для загрузки фрагментов
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // Заменяем содержимое FrameLayout
                .commit();
    }

    // Обработка нажатия кнопки "назад"
    @Override
    public void onBackPressed() {
        // Если Drawer открыт, закрываем его
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Иначе стандартное поведение кнопки "назад"
            super.onBackPressed();
        }
    }

    // Этот метод нужен, чтобы ActionBarDrawerToggle корректно обрабатывал
    // нажатие на иконку-гамбургер в ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true; // ActionBarDrawerToggle обработал нажатие
        }
        return super.onOptionsItemSelected(item);
    }

    // Переопределяем onActivityResult, чтобы передать его во вложенный фрагмент
    // (нужно, если фрагмент использует startActivityForResult)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Находим текущий видимый фрагмент и передаем ему результат
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}