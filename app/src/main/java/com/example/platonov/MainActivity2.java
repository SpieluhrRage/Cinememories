package com.example.platonov;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView messageText = new TextView(this);
        messageText.setTextSize(26);
        messageText.setPadding(16, 16, 16, 16);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null){
            String fio = arguments.get("fio").toString();
            String group = arguments.get("group").toString();
            int age = arguments.getInt("age");
            int mark = arguments.getInt("mark");
            messageText.setText("FIO: " + fio + "\nGroup: " + group + "\nAge: " + age + "\nMark: " + mark);

        }
        setContentView(messageText);
    }


}