package com.example.platonov;

import android.animation.ObjectAnimator;
import android.app.*;
import android.content.*;
import android.media.MediaPlayer;
import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "cinema_channel";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        findViewById(R.id.openWeb).setOnClickListener(v -> {
            Toast.makeText(this, "Открытие каталога", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, WebViewActivity.class));
        });

        findViewById(R.id.playMusic).setOnClickListener(v -> {
            Toast.makeText(this, "Саундтрек: play/pause", Toast.LENGTH_SHORT).show();
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3");
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                else mediaPlayer.start();
            }
        });

        findViewById(R.id.animate).setOnClickListener(v -> {
            Toast.makeText(this, "Анимация запущена", Toast.LENGTH_SHORT).show();
            TextView target = findViewById(R.id.animateTarget);
            ObjectAnimator anim = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f);
            anim.setDuration(1000);
            anim.start();
        });

        findViewById(R.id.notifyNow).setOnClickListener(v -> {
            Toast.makeText(this, "Уведомление отправлено", Toast.LENGTH_SHORT).show();
            sendNotification("Фильмотека", "Не забудь посмотреть новый фильм!");
        });

        findViewById(R.id.notifyLater).setOnClickListener(v -> {
            Toast.makeText(this, "Напоминание через 10 сек", Toast.LENGTH_SHORT).show();
            scheduleNotification(10000);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Movie Reminder Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, builder.build());
    }

    private void scheduleNotification(long delay) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + delay, pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
