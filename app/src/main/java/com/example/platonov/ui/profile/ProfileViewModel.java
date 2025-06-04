package com.example.platonov.ui.profile;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.platonov.data.database.AppDatabase;

/**
 * ViewModel для экрана «Профиль».
 * Здесь реализуем логику выхода (очистка БД) и работы с настройками.
 */
public class ProfileViewModel extends AndroidViewModel {

    private final AppDatabase database;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    /**
     * Очищает всю локальную базу (удаляет все таблицы).
     * Затем View (Fragment) может запустить навигацию к экрану логина.
     */
    public void logout() {
        // Очищаем ВСЁ: MovieEntity и др.
        new Thread(() -> {
            // Room.execute ничего не возвращает, просто очищаем все таблицы.
            database.clearAllTables();
        }).start();
    }
}