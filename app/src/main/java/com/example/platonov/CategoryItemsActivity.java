package com.example.platonov;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class CategoryItemsActivity extends AppCompatActivity {

    private TextView categoryTitleTextView;
    private ListView itemsListView;
    private LinearLayout addRemoveLayout;
    private EditText newItemEditText;

    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> itemsList; // Список для этой категории
    private ArrayList<String> selectedItems; // Список выбранных для удаления
    private String categoryName;
    private boolean isWatchlist = false; // Флаг для категории "Посмотреть позже"

    // Данные (заглушки, по-хорошему, их нужно получать из базы/хранилища)
    private ArrayList<Movie> allMovies = new ArrayList<>(); // Тут должен быть доступ к общему списку фильмов

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        categoryTitleTextView = findViewById(R.id.categoryTitleTextView);
        itemsListView = findViewById(R.id.itemsListView);
        addRemoveLayout = findViewById(R.id.addRemoveLayout);
        newItemEditText = findViewById(R.id.newItemEditText);

        itemsList = new ArrayList<>();
        selectedItems = new ArrayList<>();

        // Получаем имя категории из Intent
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName == null) {
            categoryName = "Ошибка"; // Обработка ошибки
        }
        categoryTitleTextView.setText(categoryName);

        // --- Определяем, какая категория и заполняем список ---
        if (categoryName.equals("Watch later")) {
            isWatchlist = true;
            addRemoveLayout.setVisibility(View.VISIBLE); // Показываем блок добавления/удаления
            // TODO: Загрузить сохраненный список "Посмотреть позже" (например, из SharedPreferences или БД)
            // Пока просто добавим пару элементов для примера
            itemsList.add("Фильм A");
            itemsList.add("Фильм B");

            // Используем адаптер с multiple choice layout
            itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_multiple_choice, itemsList);

        } else {
            isWatchlist = false;
            addRemoveLayout.setVisibility(View.GONE); // Скрываем блок

            // TODO: Загрузить ВСЕ фильмы (из MainScreen/БД) в allMovies
            // loadAllMoviesFromDatabase(); // Примерный метод

            // Фильтруем фильмы по жанру (используем categoryName)
            for (Movie movie : allMovies) {
                if (movie.getGenre() != null && movie.getGenre().equalsIgnoreCase(categoryName)) {
                    itemsList.add(movie.getTitle()); // Добавляем только названия
                }
            }
            // Используем обычный адаптер
            itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, itemsList);
        }

        itemsListView.setAdapter(itemsAdapter);

        // Устанавливаем слушатель только если это Watchlist (для выбора элементов для удаления)
        if (isWatchlist) {
            itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = itemsAdapter.getItem(position);
                    if (item != null) {
                        if (itemsListView.isItemChecked(position)) {
                            if (!selectedItems.contains(item)) { // Избегаем дубликатов
                                selectedItems.add(item);
                            }
                        } else {
                            selectedItems.remove(item);
                        }
                    }
                }
            });
        } else {
            // Для других категорий можно добавить переход на экран деталей фильма
            itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: Найти объект Movie по названию itemsList.get(position) и передать его в MovieDetailActivity
                    Toast.makeText(CategoryItemsActivity.this, "Нажат: " + itemsList.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Метод для кнопки "Добавить" (вызывается через android:onClick в XML)
    public void addWatchlistItem(View view) {
        if (!isWatchlist) return; // Защита

        String newItem = newItemEditText.getText().toString().trim();
        if (!newItem.isEmpty()) {
            itemsAdapter.add(newItem); // Добавляем в адаптер (и в itemsList)
            newItemEditText.setText(""); // Очищаем поле ввода
            itemsAdapter.notifyDataSetChanged(); // Обновляем ListView
            // TODO: Сохранить обновленный список itemsList (в SharedPreferences/БД)
            Toast.makeText(this, "Добавлено: " + newItem, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для кнопки "Удалить выбранные" (вызывается через android:onClick в XML)
    public void removeWatchlistItems(View view) {
        if (!isWatchlist) return; // Защита

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Нет выбранных элементов для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        // Удаляем выбранные элементы из адаптера (и itemsList)
        for (String itemToRemove : selectedItems) {
            itemsAdapter.remove(itemToRemove);
        }

        // Снимаем все отметки в ListView
        itemsListView.clearChoices();
        // Очищаем список выбранных
        selectedItems.clear();
        // Обновляем ListView
        itemsAdapter.notifyDataSetChanged();
        // TODO: Сохранить обновленный список itemsList (в SharedPreferences/БД)
        Toast.makeText(this, "Выбранные элементы удалены", Toast.LENGTH_SHORT).show();
    }
}