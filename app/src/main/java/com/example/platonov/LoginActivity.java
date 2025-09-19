package com.example.platonov;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.platonov.data.database.AppDatabase;
import com.example.platonov.data.dao.UserDao;
import com.example.platonov.data.entity.UserEntity;
import com.example.platonov.data.prefs.SessionManager;
import com.example.platonov.databinding.ActivityLoginBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isRegisterMode = false;

    private ExecutorService io = Executors.newSingleThreadExecutor();
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = AppDatabase.getInstance(getApplicationContext()).userDao();
        session = new SessionManager(this);

        // Если уже авторизован — сразу в MainActivity
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        binding.buttonRegisterMode.setOnClickListener(v -> toggleMode());

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String pass  = binding.editTextPassword.getText().toString().trim();
            String confirm = binding.editTextConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.editTextEmail.setError("Enter email");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                binding.editTextPassword.setError("Enter password");
                return;
            }

            if (isRegisterMode) {
                // Регистрация
                if (TextUtils.isEmpty(confirm)) {
                    binding.editTextConfirm.setError("Confirm password");
                    return;
                }
                if (!pass.equals(confirm)) {
                    binding.editTextConfirm.setError("Passwords do not match");
                    return;
                }

                io.execute(() -> {
                    UserEntity exists = userDao.getByEmail(email);
                    if (exists != null) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }
                    long id = userDao.insert(new UserEntity(email, pass));
                    session.saveUserId(id);
                    runOnUiThread(this::goToMain);
                });

            } else {
                // Вход
                io.execute(() -> {
                    UserEntity user = userDao.login(email, pass);
                    if (user == null) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        );
                        return;
                    }
                    session.saveUserId(user.id);
                    runOnUiThread(this::goToMain);
                });
            }
        });

        // Стартовый режим — «Login»
        applyMode();
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        applyMode();
    }

    private void applyMode() {
        binding.textViewLoginTitle.setText(isRegisterMode ? "Register" : "Login");
        binding.editTextConfirm.setVisibility(isRegisterMode ? View.VISIBLE : View.GONE);
        binding.buttonLogin.setText(isRegisterMode ? "Create account" : "Login");
        binding.buttonRegisterMode.setText(isRegisterMode ? "Back to Login" : "Register mode");
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        io.shutdown();
    }
}
