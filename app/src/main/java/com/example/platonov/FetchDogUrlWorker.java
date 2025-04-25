package com.example.platonov;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data; // Для передачи результата
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONException; // Для обработки JSON
import org.json.JSONObject; // Для обработки JSON
import java.io.BufferedReader; // Для чтения ответа
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection; // Для HTTP запроса
import java.net.URL;

public class FetchDogUrlWorker extends Worker {
    private static final String TAG = "FetchDogUrlWorker";
    public static final String KEY_RESULT_URL = "dog_image_url";

    public FetchDogUrlWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String apiUrl = "https://random.dog/woof.json";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            URL url = new URL(apiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                Log.e(TAG, "InputStream is null");
                return Result.failure();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                Log.e(TAG, "Response buffer is empty");
                return Result.failure();
            }
            jsonString = buffer.toString();
            Log.d(TAG, "JSON Response: " + jsonString);

            // Парсинг JSON
            JSONObject jsonObject = new JSONObject(jsonString);
            String imageUrl = jsonObject.getString("url"); // Получаем URL картинки

            // Проверяем, что это не видео (иногда API возвращает mp4/webm)
            if (imageUrl.toLowerCase().endsWith(".mp4") || imageUrl.toLowerCase().endsWith(".webm")) {
                Log.w(TAG, "Received a video URL, retrying...");
                return Result.retry(); // Говорим WorkManager'у попробовать еще раз
            }


            // Передаем URL как результат
            Data outputData = new Data.Builder()
                    .putString(KEY_RESULT_URL, imageUrl)
                    .build();

            Log.d(TAG, "Image URL fetched: " + imageUrl);
            return Result.success(outputData); // Возвращаем успех с данными

        } catch (IOException e) {
            Log.e(TAG, "Error during HTTP request", e);
            return Result.failure();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            return Result.failure();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }
}