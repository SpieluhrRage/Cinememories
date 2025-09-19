package com.example.platonov.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.platonov.data.dao.MovieDao;
import com.example.platonov.data.dao.UserDao;
import com.example.platonov.data.entity.MovieEntity;
import com.example.platonov.data.entity.UserEntity;

@Database(
        entities = { MovieEntity.class, UserEntity.class },
        version = 10,          // ↑ подними версию, чтобы БД точно пересоздалась
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract MovieDao movieDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db"
                            )
                            .fallbackToDestructiveMigration()   // разрешаем разрушительную миграцию
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
