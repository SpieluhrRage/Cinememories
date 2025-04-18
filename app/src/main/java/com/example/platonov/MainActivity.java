package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // --- Запуск сервиса по кнопке ---
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(MainActivity.this, MusicService.class);
                startService(startIntent);
                // Можно деактивировать кнопку запуска и активировать кнопку остановки
                // startButton.setEnabled(false);
                // stopButton.setEnabled(true);
            }
        });

        // --- Остановка сервиса по кнопке ---
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, MusicService.class);
                stopService(stopIntent);
                // Можно активировать кнопку запуска и деактивировать кнопку остановки
                // startButton.setEnabled(true);
                // stopButton.setEnabled(false);
            }
        });

        // Опционально: Запуск сервиса при старте Activity (как в методичке, но без явного вызова)
        // Intent startIntentOnCreate = new Intent(this, MusicService.class);
        // startService(startIntentOnCreate);
    }

    // Опционально: Остановка сервиса при уничтожении Activity (как в методичке)
    // @Override
    // protected void onDestroy() {
    //     super.onDestroy();
    //     // Остановка сервиса и музыки при уничтожении активности
    //     Intent stopIntent = new Intent(this, MusicService.class);
    //     stopService(stopIntent);
    // }
}