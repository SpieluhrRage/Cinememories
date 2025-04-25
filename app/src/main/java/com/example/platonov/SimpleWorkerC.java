package com.example.platonov;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SimpleWorkerC extends Worker {
    private static final String TAG = "SimpleWorkerC";
    public SimpleWorkerC(@NonNull Context context, @NonNull WorkerParameters workerParams) { super(context, workerParams); }
    @NonNull @Override public Result doWork() {
        Log.d(TAG, "Задача C: Начало работы...");
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); Log.e(TAG, "Задача C: Прервана", e); return Result.failure(); }
        Log.d(TAG, "Задача C: Работа завершена.");
        return Result.success();
    }
}
