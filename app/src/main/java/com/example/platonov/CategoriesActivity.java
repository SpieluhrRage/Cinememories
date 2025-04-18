package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ListView categoriesListView = findViewById(R.id.categoriesListView);
        final String[] categories = getResources().getStringArray(R.array.movie_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];

                if (selectedCategory.equals("All movies")) {
                    Intent intent = new Intent(CategoriesActivity.this, MainScreen.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CategoriesActivity.this, CategoryItemsActivity.class);
                    intent.putExtra("CATEGORY_NAME", selectedCategory);
                    startActivity(intent);
                }
            }
        });
    }
}