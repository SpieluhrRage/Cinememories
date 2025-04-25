package com.example.platonov;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SimpleWorkerA extends Worker {
    private static final String TAG = "SimpleWorkerA";

    public SimpleWorkerA(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Задача A: Начало работы...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Задача A: Прервана", e);
            return Result.failure();
        }
        Log.d(TAG, "Задача A: Работа завершена.");
        return Result.success();
    }
}