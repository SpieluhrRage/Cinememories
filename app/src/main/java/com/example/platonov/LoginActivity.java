package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.platonov.databinding.ActivityLoginBinding;

/**
 * Простейшая LoginActivity.
 * Вполне можно заменить позже реальной проверкой через TMDb API.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Используем ViewBinding для сетки activity_login.xml
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Находим поля и кнопку
        EditText editTextEmail = binding.editTextEmail;
        EditText editTextPassword = binding.editTextPassword;
        Button buttonLogin = binding.buttonLogin;

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Простая валидация: оба поля должны быть не пустые
            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Enter email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Enter password");
                return;
            }

            // TODO: здесь можно добавить реальную проверку учётных данных через TMDb API
            // Пока просто переходим в MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            // Очищаем back-stack, чтобы при нажатии back из MainActivity не возвращаться на LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
