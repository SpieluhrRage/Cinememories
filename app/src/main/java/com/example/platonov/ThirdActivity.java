package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private EditText editTextDay;
    private EditText editTextTime;
    private EditText editTextComments;
    private Button buttonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        editTextDay = findViewById(R.id.editTextDay);
        editTextTime = findViewById(R.id.editTextTime);
        editTextComments = findViewById(R.id.editTextComments);
        buttonOK = findViewById(R.id.buttonOK);

        buttonOK.setOnClickListener(v -> {
            String day = editTextDay.getText().toString();
            String time = editTextTime.getText().toString();
            String comments = editTextComments.getText().toString();

            String timeInfo = "День: " + day + ", Время: " + time + ", Комментарии: " + comments;

            Intent resultIntent = new Intent();
            resultIntent.putExtra("timeInfo", timeInfo);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}