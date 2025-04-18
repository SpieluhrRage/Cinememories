package com.example.platonov.fragments;

import android.app.Dialog; // Для Custom Dialog
import android.app.TimePickerDialog;
import android.content.Context; // Для доступа к ресурсам/контексту
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button; // Для кнопок в диалоге
import android.widget.EditText; // Для поля ввода в диалоге
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.platonov.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // Для FAB

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WishlistFragment extends Fragment {

    private ListView wishlistListView;
    private FloatingActionButton fabAddWishlistItem;
    private ArrayList<String> wishlistItems; // Список названий фильмов
    private ArrayAdapter<String> itemsAdapter;

    // Переменные для хранения последнего выбранного времени (опционально, для удобства)
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализируем список здесь, чтобы он сохранялся при пересоздании View
        wishlistItems = new ArrayList<>();
        // TODO: Загрузить сохраненный wishlistItems из SharedPreferences или БД
        // loadWishlist();
        // Добавим начальные данные для примера, если список пуст
        if (wishlistItems.isEmpty()){
            wishlistItems.add("Пример фильма 1 (нажми для установки времени)");
            wishlistItems.add("Пример фильма 2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        wishlistListView = view.findViewById(R.id.wishlistListView);
        fabAddWishlistItem = view.findViewById(R.id.fabAddWishlistItem);

        // Используем requireContext() для получения не-null контекста
        Context context = requireContext();

        // Создаем и устанавливаем адаптер
        itemsAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, // Простой макет для одного элемента
                wishlistItems);
        wishlistListView.setAdapter(itemsAdapter);

        // --- Обработчик нажатия на элемент списка (для TimePickerDialog) ---
        wishlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMovie = itemsAdapter.getItem(position);
                showTimePickerDialog(context, selectedMovie); // Вызываем метод показа TimePickerDialog
            }
        });

        // --- Обработчик нажатия на FAB (для Custom Dialog) ---
        fabAddWishlistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMovieDialog(context); // Вызываем метод показа Custom Dialog
            }
        });

        return view;
    }

    private void showTimePickerDialog(Context context, final String movieTitle) {
        final Calendar c = Calendar.getInstance();
        int hour = (lastSelectedHour != -1) ? lastSelectedHour : c.get(Calendar.HOUR_OF_DAY);
        int minute = (lastSelectedMinute != -1) ? lastSelectedMinute : c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        lastSelectedHour = hourOfDay; // Сохраняем для удобства
                        lastSelectedMinute = minute;
                        String timeString = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        Toast.makeText(context, "Установить напоминание для '" + movieTitle + "' на " + timeString + "?", Toast.LENGTH_LONG).show();

                    }
                }, hour, minute, true);
        timePickerDialog.setTitle("Установить время напоминания");
        timePickerDialog.show();
    }

    // --- Метод для показа Custom Dialog добавления фильма ---
    private void showAddMovieDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_and_wishlist_item);
        dialog.setTitle("Добавить фильм");

        EditText movieTitleEditText = dialog.findViewById(R.id.movieTitleEditText);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        Button addButton = dialog.findViewById(R.id.add_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            String movieTitle = movieTitleEditText.getText().toString().trim();
            if (!movieTitle.isEmpty()) {
                wishlistItems.add(movieTitle);
                itemsAdapter.notifyDataSetChanged();
                Toast.makeText(context, "'" + movieTitle + "' добавлен в список", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Введите название фильма", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


}