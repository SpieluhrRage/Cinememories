package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer; // Для наблюдения за WorkInfo
import androidx.work.Data; // Для данных
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide; // Импорт Glide

import java.util.Arrays; // Для создания списка
import java.util.List; // Для списка

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnSequential;
    private Button btnParallel;
    private Button btnLoadImage;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSequential = findViewById(R.id.btnSequential);
        btnParallel = findViewById(R.id.btnParallel);
        btnLoadImage = findViewById(R.id.btnLoadImage);
        imageView = findViewById(R.id.imageView);

        // Задание 1: Последовательный запуск
        btnSequential.setOnClickListener(v -> startSequentialWork());

        // Задание 2: Параллельный запуск
        btnParallel.setOnClickListener(v -> startParallelWork());

        // Задание 3: Загрузка изображения
        btnLoadImage.setOnClickListener(v -> startImageLoadingWork());
    }

    // --- Задание 1 ---
    private void startSequentialWork() {
        Log.d(TAG, "Запуск последовательных задач...");
        Toast.makeText(this, "Запуск последовательных задач...", Toast.LENGTH_SHORT).show();

        OneTimeWorkRequest workA = new OneTimeWorkRequest.Builder(SimpleWorkerA.class).build();
        OneTimeWorkRequest workB = new OneTimeWorkRequest.Builder(SimpleWorkerB.class).build();
        OneTimeWorkRequest workC = new OneTimeWorkRequest.Builder(SimpleWorkerC.class).build();

        WorkManager.getInstance(getApplicationContext())
                .beginWith(workA)
                .then(workB)
                .then(workC)
                .enqueue();

        // Наблюдение за последней задачей (опционально)
        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(workC.getId())
                .observe(this, info -> {
                    if (info != null && info.getState().isFinished()) {
                        Log.d(TAG, "Последовательная цепочка завершена. Состояние C: " + info.getState());
                        Toast.makeText(MainActivity.this, "Последовательные задачи завершены", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Задание 2 ---
    private void startParallelWork() {
        Log.d(TAG, "Запуск параллельных задач...");
        Toast.makeText(this, "Запуск параллельных задач...", Toast.LENGTH_SHORT).show();

        OneTimeWorkRequest workB1 = new OneTimeWorkRequest.Builder(SimpleWorkerB.class)
                .addTag("parallel_tag")
                .build();
        OneTimeWorkRequest workC1 = new OneTimeWorkRequest.Builder(SimpleWorkerC.class)
                .addTag("parallel_tag")
                .build();

        List<OneTimeWorkRequest> parallelWorks = Arrays.asList(workB1, workC1);

        WorkManager.getInstance(getApplicationContext()).enqueue(parallelWorks);


        WorkManager.getInstance(getApplicationContext()).getWorkInfosByTagLiveData("parallel_tag")
                .observe(this, workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) return;

                    boolean allFinished = true;
                    for (WorkInfo info : workInfos) {
                        if (!info.getState().isFinished()) {
                            allFinished = false;
                            break;
                        }
                    }

                    if (allFinished) {
                        Log.d(TAG, "Параллельные задачи завершены.");
                        Toast.makeText(MainActivity.this, "Параллельные задачи завершены", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Задание 3 ---
    private void startImageLoadingWork() {
        Log.d(TAG, "Запуск загрузки URL изображения...");
        Toast.makeText(this, "Загрузка URL...", Toast.LENGTH_SHORT).show();
        imageView.setImageResource(android.R.color.darker_gray);

        OneTimeWorkRequest fetchRequest =
                new OneTimeWorkRequest.Builder(FetchDogUrlWorker.class).build();

        WorkManager.getInstance(getApplicationContext()).enqueue(fetchRequest);

        WorkManager.getInstance(getApplicationContext())
                .getWorkInfoByIdLiveData(fetchRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null) {
                            Log.d(TAG, "Состояние загрузки URL: " + workInfo.getState());
                            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                Data outputData = workInfo.getOutputData();
                                String imageUrl = outputData.getString(FetchDogUrlWorker.KEY_RESULT_URL);
                                Log.d(TAG, "URL получен: " + imageUrl);

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Toast.makeText(MainActivity.this, "URL получен, загрузка картинки...", Toast.LENGTH_SHORT).show();
                                    Glide.with(MainActivity.this)
                                            .load(imageUrl)
                                            .placeholder(android.R.color.darker_gray)
                                            .error(android.R.drawable.ic_menu_report_image) // Картинка при ошибке
                                            .into(imageView);
                                } else {
                                    Log.e(TAG, "Получен пустой URL");
                                    Toast.makeText(MainActivity.this, "Ошибка: получен пустой URL", Toast.LENGTH_SHORT).show();
                                }
                                WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(fetchRequest.getId()).removeObserver(this);

                            } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                                Log.e(TAG, "Загрузка URL не удалась");
                                Toast.makeText(MainActivity.this, "Ошибка загрузки URL", Toast.LENGTH_SHORT).show();
                                imageView.setImageResource(android.R.drawable.ic_menu_report_image); // Ошибка
                                WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(fetchRequest.getId()).removeObserver(this);
                            } else if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                                Log.w(TAG, "Загрузка URL отменена");
                                Toast.makeText(MainActivity.this, "Загрузка отменена", Toast.LENGTH_SHORT).show();
                                WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(fetchRequest.getId()).removeObserver(this);
                            }
                        }
                    }
                });
    }
}