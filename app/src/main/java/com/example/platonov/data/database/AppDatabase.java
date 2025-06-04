package com.example.platonov.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.platonov.data.dao.MovieDao;
import com.example.platonov.data.entity.MovieEntity;

/**
 * Основной класс базы данных Room.
 * Включает в себя все сущности (Entity) и DAO-интерфейсы.
 */
@Database(
        entities = {
                MovieEntity.class
                // Если появятся другие Entity, их добавляем через запятую в этот список
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Абстрактный метод для получения DAO
    public abstract MovieDao movieDao();

    // Единственный статический экземпляр AppDatabase
    private static volatile AppDatabase INSTANCE;

    /**
     * Метод для получения Singleton-экземпляра базы данных.
     * @param context - контекст приложения
     * @return AppDatabase - единственный экземпляр
     */
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Создаём базу данных
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "filmoteka_db"
                            )
                            // В случае миграций, пока просто сбрасываем БД
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback) // если нужно выполнить действия при создании/открытии БД
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Пример callback’а, если нужно что-то сделать сразу после создания БД.
     * В нём можно, например, предварительно наполнить таблицу тестовыми данными.
     */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Здесь можно выполнить операции сразу после создания БД, например:
            // new Thread(() -> {
            //     AppDatabase database = INSTANCE;
            //     // допустим, вставим тестовый фильм
            //     database.movieDao().insertMovie(
            //         new MovieEntity("Test Movie", 2023, "Genre", null, "Synopsis", false, false, 0f, "")
            //     );
            // }).start();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Здесь можно выполнить действия при каждом открытии БД
        }
    };
}