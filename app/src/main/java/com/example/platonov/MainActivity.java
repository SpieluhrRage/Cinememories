package com.example.platonov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        Button btn = findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View view){
                EditText fiotxt = findViewById(R.id.text_input1);
                EditText grouptxt = findViewById(R.id.text_input2);
                EditText agetxt = findViewById(R.id.text_input3);
                EditText marktxt = findViewById(R.id.text_input4);

                String fio = fiotxt.getText().toString();
                String group = grouptxt.getText().toString();
                int age = Integer.parseInt(agetxt.getText().toString());
                int mark = Integer.parseInt(marktxt.getText().toString());

                Intent reg = new Intent(MainActivity.this, MainActivity2.class);
                reg.putExtra("fio", fio);
                reg.putExtra("group", group);
                reg.putExtra("age", age);
                reg.putExtra("mark", mark);
                startActivity(reg);
            }
        });



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

    public void onNextActivity(View view){
        EditText fiotxt = findViewById(R.id.text_input1);
        EditText grouptxt = findViewById(R.id.text_input2);
        EditText agetxt = findViewById(R.id.text_input3);
        EditText marktxt = findViewById(R.id.text_input4);

        String fio = fiotxt.getText().toString();
        String group = grouptxt.getText().toString();
        int age = Integer.parseInt(agetxt.getText().toString());
        int mark = Integer.parseInt(marktxt.getText().toString());

        Intent reg = new Intent(this, MainActivity2.class);
        reg.putExtra("fio", fio);
        reg.putExtra("group", group);
        reg.putExtra("age", age);
        reg.putExtra("mark", mark);
        startActivity(reg);
    }


}