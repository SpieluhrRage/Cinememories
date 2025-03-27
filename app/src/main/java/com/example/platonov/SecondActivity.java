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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SecondActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewSurname;
    private EditText editTextSubject;
    private Button buttonEnterInfo;
    private ActivityResultLauncher<Intent> thirdActivityResultLauncher;

    private Button buttonStatic;
    private Button buttonDynamic;
    private Button buttonContainer;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewName = findViewById(R.id.textViewName);
        textViewSurname = findViewById(R.id.textViewSurname);
        editTextSubject = findViewById(R.id.editTextSubject);
        buttonEnterInfo = findViewById(R.id.buttonEnterInfo);

        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");

        textViewName.setText("Имя: " + name);
        textViewSurname.setText("Фамилия: " + surname);

        thirdActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String timeInfo = data.getStringExtra("timeInfo");
                            Toast.makeText(SecondActivity.this, "Время занятия передано: " + timeInfo, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        buttonEnterInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
            thirdActivityResultLauncher.launch(intent);
        });

        buttonStatic = findViewById(R.id.buttonStatic);
        buttonDynamic = findViewById(R.id.buttonDynamic);
        buttonContainer = findViewById(R.id.buttonContainer);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        buttonStatic.setOnClickListener(v -> {
            showFragment(new StaticFragment());
        });

        buttonDynamic.setOnClickListener(v -> {
            showFragment(new DynamicFragment());
        });

        buttonContainer.setOnClickListener(v -> {
            showFragment(new ContainerFragment());
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}