package com.example.platonov;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest; // Для разрешений
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build; // Для проверки версии Android
import android.os.Bundle;
import android.os.Environment; // Для внешнего хранилища
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File; // Для работы с File объектами
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader; // Для внешнего чтения
import java.io.FileWriter; // Для внешней записи
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityFiles";
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Ключи для сохранения состояния (Задание 2)
    private static final String KEY_FILENAME = "filename_state";
    private static final String KEY_CONTENT = "content_state";
    private static final String KEY_OUTPUT = "output_state";

    EditText editFileName, editFileContent;
    Button btnSave, btnAppend, btnRead, btnDelete;
    Button btnSaveExternal, btnReadExternal, btnDeleteExternal, btnOpenReaderApp;
    TextView tvFileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editFileName = findViewById(R.id.editFileName);
        editFileContent = findViewById(R.id.editFileContent);
        btnSave = findViewById(R.id.btnSave);
        btnAppend = findViewById(R.id.btnAppend);
        btnRead = findViewById(R.id.btnRead);
        btnDelete = findViewById(R.id.btnDelete);
        tvFileContent = findViewById(R.id.tvFileContent);
        btnSaveExternal = findViewById(R.id.btnSaveExternal);
        btnReadExternal = findViewById(R.id.btnReadExternal);
        btnDeleteExternal = findViewById(R.id.btnDeleteExternal);


        // --- Задание 1: Внутреннее хранилище ---
        btnSave.setOnClickListener(v -> saveToFile(Context.MODE_PRIVATE));
        btnAppend.setOnClickListener(v -> saveToFile(Context.MODE_APPEND));
        btnRead.setOnClickListener(v -> readFileContent());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        // --- Задание 3: Внешнее хранилище ---
        btnSaveExternal.setOnClickListener(v -> saveToExternalStorage());
        btnReadExternal.setOnClickListener(v -> readFromExternalStorage());
        btnDeleteExternal.setOnClickListener(v -> deleteFromExternalStorage());
         // Для проверки чтения из App 2

        // --- Задание 2: Восстановление состояния ---
        if (savedInstanceState != null) {
            editFileName.setText(savedInstanceState.getString(KEY_FILENAME, ""));
            editFileContent.setText(savedInstanceState.getString(KEY_CONTENT, ""));
            tvFileContent.setText(savedInstanceState.getString(KEY_OUTPUT, ""));
            Log.d(TAG, "State restored");
        }
    }

    // --- Задание 2: Сохранение состояния ---
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FILENAME, editFileName.getText().toString());
        outState.putString(KEY_CONTENT, editFileContent.getText().toString());
        outState.putString(KEY_OUTPUT, tvFileContent.getText().toString());
        Log.d(TAG, "State saved");
    }

    // --- Методы для внутреннего хранилища (Задание 1) ---

    private void saveToFile(int mode) {
        String filename = editFileName.getText().toString().trim();
        String content = editFileContent.getText().toString();

        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(filename, mode);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            if (mode == Context.MODE_APPEND) {
                fos.write("\n".getBytes(StandardCharsets.UTF_8));
            }
            Toast.makeText(this, "Сохранено в " + getFilesDir() + "/" + filename, Toast.LENGTH_LONG).show();
            Log.i(TAG, "Saved to internal: " + filename);
            editFileContent.setText("");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found for writing: " + filename, e);
            Toast.makeText(this, "Ошибка: Файл не найден", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file: " + filename, e);
            Toast.makeText(this, "Ошибка записи в файл", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing FileOutputStream", e);
                }
            }
        }
    }

    private void readFileContent() {
        String filename = editFileName.getText().toString().trim();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }

        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            }
            tvFileContent.setText(stringBuilder.toString());
            Log.i(TAG, "Read from internal: " + filename);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "File not found for reading: " + filename);
            tvFileContent.setText(""); // Очищаем, если файла нет
            Toast.makeText(this, "Файл не найден: " + filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + filename, e);
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing FileInputStream", e);
                }
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        final String filename = editFileName.getText().toString().trim();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Удаление файла")
                .setMessage("Вы уверены, что хотите удалить файл '" + filename + "'?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Удалить", (dialog, whichButton) -> deleteInternalFile(filename))
                .setNegativeButton("Отмена", null)
                .show();
    }


    private void deleteInternalFile(String filename) {
        if (deleteFile(filename)) {
            Toast.makeText(this, "Файл '" + filename + "' удален", Toast.LENGTH_SHORT).show();
            tvFileContent.setText(""); // Очищаем вывод
            Log.i(TAG, "Deleted internal file: " + filename);
        } else {
            Toast.makeText(this, "Не удалось удалить файл '" + filename + "'", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Failed to delete internal file: " + filename);
        }
    }

    // --- Методы для внешнего хранилища (Задание 3) ---

    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), STORAGE_PERMISSION_CODE);
                return false; // Разрешения еще не предоставлены
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean allGranted = true;
                for(int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                if(allGranted){
                    Toast.makeText(this, "Разрешения получены!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Разрешения не предоставлены!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private File getExternalDocumentDir(String filename) {

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "Внешнее хранилище недоступно", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "External storage not mounted");
            return null;
        }

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "Failed to create external documents directory");
            }
        }
        Log.d(TAG, "External storage dir: " + storageDir.getAbsolutePath());
        return new File(storageDir, filename);
    }


    private void saveToExternalStorage() {
        if (!checkAndRequestPermissions()) {
            return;
        }
        String filename = editFileName.getText().toString().trim();
        String content = editFileContent.getText().toString();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = getExternalDocumentDir(filename);
        if (file == null) return;
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.append(content);
            writer.flush();
            Toast.makeText(this, "Сохранено во внешнее: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Saved to external: " + filename);
            editFileContent.setText("");
        } catch (IOException e) {
            Log.e(TAG, "Error writing to external file: " + filename, e);
            Toast.makeText(this, "Ошибка записи во внешний файл", Toast.LENGTH_SHORT).show();
        }
    }

    private void readFromExternalStorage() {
        if (!checkAndRequestPermissions()) {
            return;
        }
        String filename = editFileName.getText().toString().trim();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = getExternalDocumentDir(filename);
        if (file == null || !file.exists()) {
            Toast.makeText(this, "Внешний файл не найден: " + filename, Toast.LENGTH_SHORT).show();
            tvFileContent.setText("");
            Log.w(TAG, "External file not found: " + filename);
            return;
        }

        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            tvFileContent.setText(text.toString());
            Log.i(TAG, "Read from external: " + filename);
        } catch (IOException e) {
            Log.e(TAG, "Error reading external file: " + filename, e);
            Toast.makeText(this, "Ошибка чтения внешнего файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFromExternalStorage() {
        if (!checkAndRequestPermissions()) {
            return;
        }
        String filename = editFileName.getText().toString().trim();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите имя файла для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = getExternalDocumentDir(filename);
        if (file == null) return;

        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(this, "Внешний файл удален: " + filename, Toast.LENGTH_SHORT).show();
                tvFileContent.setText("");
                Log.i(TAG, "Deleted external file: " + filename);
            } else {
                Toast.makeText(this, "Не удалось удалить внешний файл", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to delete external file: " + filename);
            }
        } else {
            Toast.makeText(this, "Внешний файл не найден для удаления", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "External file not found for deletion: " + filename);
        }
    }



}

// Не забудьте добавить необходимые импорты (List, ArrayList и т.д.)