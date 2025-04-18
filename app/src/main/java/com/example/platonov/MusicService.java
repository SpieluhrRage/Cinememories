package com.example.platonov;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log; // Можно оставить Log для отладки, но не использовать для выполнения задания
import androidx.annotation.Nullable;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(1.0f, 1.0f);
            Log.d(TAG, "MediaPlayer created and configured.");
        } else {
            Log.e(TAG, "Error creating MediaPlayer. Check if music file exists in res/raw.");
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d(TAG, "Music started playing.");
        } else if (mediaPlayer == null) {
            Log.e(TAG, "MediaPlayer is null in onStartCommand.");
        } else {
            Log.d(TAG, "Music is already playing.");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
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