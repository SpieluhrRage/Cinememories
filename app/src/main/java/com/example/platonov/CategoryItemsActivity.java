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
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName == null) {
            categoryName = "Ошибка";
        }
        categoryTitleTextView.setText(categoryName);
        if (categoryName.equals("Watch later")) {
            isWatchlist = true;
            addRemoveLayout.setVisibility(View.VISIBLE);
            itemsList.add("Фильм A");
            itemsList.add("Фильм B");
            itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_multiple_choice, itemsList);

        } else {
            isWatchlist = false;
            addRemoveLayout.setVisibility(View.GONE);
            for (Movie movie : allMovies) {
                if (movie.getGenre() != null && movie.getGenre().equalsIgnoreCase(categoryName)) {
                    itemsList.add(movie.getTitle());
                }
            }

            itemsAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, itemsList);
        }

        itemsListView.setAdapter(itemsAdapter);


        if (isWatchlist) {
            itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = itemsAdapter.getItem(position);
                    if (item != null) {
                        if (itemsListView.isItemChecked(position)) {
                            if (!selectedItems.contains(item)) {
                                selectedItems.add(item);
                            }
                        } else {
                            selectedItems.remove(item);
                        }
                    }
                }
            });
        } else {
            itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(CategoryItemsActivity.this, "Нажат: " + itemsList.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addWatchlistItem(View view) {
        if (!isWatchlist) return;

        String newItem = newItemEditText.getText().toString().trim();
        if (!newItem.isEmpty()) {
            itemsAdapter.add(newItem);
            newItemEditText.setText("");
            itemsAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Добавлено: " + newItem, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeWatchlistItems(View view) {
        if (!isWatchlist) return;
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