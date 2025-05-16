package com.example.platonov;

import android.content.Context; // Для SharedPreferences
import android.content.Intent;
import android.content.SharedPreferences; // Импорт SharedPreferences
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Для сообщений пользователю

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private Button btnSaveName, btnLoadName, btnDeleteName, btnNextActivity;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", "onCreate started");

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        editUsername = findViewById(R.id.editUsername);
        btnSaveName = findViewById(R.id.btnSaveName);
        btnLoadName = findViewById(R.id.btnLoadName);
        btnDeleteName = findViewById(R.id.btnDeleteName);
        btnNextActivity = findViewById(R.id.btnNextActivity);

        btnSaveName.setOnClickListener(v -> saveUsername());
        btnLoadName.setOnClickListener(v -> loadUsername());
        btnDeleteName.setOnClickListener(v -> deleteUsername());

        loadUsername();

        Log.i("MainActivity", "onCreate finished");
    }

    // Метод сохранения имени
    private void saveUsername() {
        String username = editUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Введите имя для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply(); // Асинхронное сохранение

        Toast.makeText(this, "Имя '" + username + "' сохранено", Toast.LENGTH_SHORT).show();
        Log.i("MainActivity", "Username saved: " + username);
    }

    // Метод загрузки имени
    private void loadUsername() {
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        editUsername.setText(savedUsername);

        if (!savedUsername.isEmpty()) {
            Toast.makeText(this, "Имя '" + savedUsername + "' загружено", Toast.LENGTH_SHORT).show();
            Log.i("MainActivity", "Username loaded: " + savedUsername);
        } else {
            Toast.makeText(this, "Сохраненное имя не найдено", Toast.LENGTH_SHORT).show();
            Log.i("MainActivity", "No username found in SharedPreferences");
        }
    }

    // Метод удаления имени
    private void deleteUsername() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        editUsername.setText("");
        Toast.makeText(this, "Сохраненное имя удалено", Toast.LENGTH_SHORT).show();
        Log.i("MainActivity", "Username deleted from SharedPreferences");
    }


    // --- Остальные методы жизненного цикла (можно оставить для логов) ---
    @Override
    protected void onStart(){
        super.onStart();
        Log.i("MainActivity", "onStart finished");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.i("MainActivity", "onResume started and finished");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.i("MainActivity", "onPause started and finished");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.i("MainActivity", "onStop started and finished");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("MainActivity", "onDestroy started and finished");
    }

    // Метод перехода (остается без изменений)
    public void onNextActivity(View view){
        Intent reg = new Intent(this, MainScreen.class);
        startActivity(reg);
    }
}