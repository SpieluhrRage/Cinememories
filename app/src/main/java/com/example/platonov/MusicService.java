package com.example.platonov;
 // Замени на имя твоего пакета

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log; // Можно оставить Log для отладки, но не использовать для выполнения задания
import androidx.annotation.Nullable;

public class MusicService extends Service {

    private static final String TAG = "MusicService"; // Для логов (опционально)
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация медиаплеера
        // Убедись, что файл music.mp3 находится в res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true); // Зацикливание воспроизведения
            mediaPlayer.setVolume(1.0f, 1.0f); // Максимальная громкость
            Log.d(TAG, "MediaPlayer created and configured.");
        } else {
            Log.e(TAG, "Error creating MediaPlayer. Check if music file exists in res/raw.");
            stopSelf(); // Останавливаем сервис, если плеер не создался
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // Начинаем воспроизведение
            Log.d(TAG, "Music started playing.");
        } else if (mediaPlayer == null) {
            Log.e(TAG, "MediaPlayer is null in onStartCommand.");
        } else {
            Log.d(TAG, "Music is already playing.");
        }
        // Возвращаем START_STICKY, чтобы сервис перезапускался, если его убьет система
        return START_STICKY;
        // Другие варианты: START_NOT_STICKY, START_REDELIVER_INTENT
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop(); // Останавливаем воспроизведение
            }
            mediaPlayer.release(); // Освобождаем ресурсы плеера
            mediaPlayer = null;
            Log.d(TAG, "MediaPlayer stopped and released.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Для сервисов без привязки возвращаем null
        return null;
    }
}