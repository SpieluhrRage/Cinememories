package com.example.platonov;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SimpleWorkerB extends Worker {
    private static final String TAG = "SimpleWorkerB";
    public SimpleWorkerB(@NonNull Context context, @NonNull WorkerParameters workerParams) { super(context, workerParams); }
    @NonNull @Override public Result doWork() {
        Log.d(TAG, "Задача B: Начало работы...");
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); Log.e(TAG, "Задача B: Прервана", e); return Result.failure(); }
        Log.d(TAG, "Задача B: Работа завершена.");
        return Result.success();
    }
}