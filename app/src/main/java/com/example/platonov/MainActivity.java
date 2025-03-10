package com.example.platonov;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.i("in method onCreate", "method start");
        Log.i("in method onCreate", "method end");
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i("in method onStart", "method start");
        Log.i("in method onStart", "method end");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("in method onResume", "method start");
        Log.i("in method onResume", "method end");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.i("in method onPause", "method start");
        Log.i("in method onPause", "method end");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("in method onStop", "method start");
        Log.i("in method onStop", "method end");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("in method onDestroy", "method start");
        Log.i("in method onDestroy", "method end");
    }
}