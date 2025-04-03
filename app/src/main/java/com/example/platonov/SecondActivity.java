package com.example.platonov;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
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
    private FragmentContainerView staticFragmentContainerView; // Для статического
    private FrameLayout dynamicFragmentContainer; // Для динамических

    private FragmentManager fragmentManager;
    private Fragment staticFragment; // Ссылка на экземпляр статического фрагмента

    private ActivityResultLauncher<Intent> thirdActivityLauncher;


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
        buttonStatic.setOnClickListener(v -> showStaticFragment());
        buttonDynamic.setOnClickListener(v -> showDynamicFragment(new DynamicFragment()));
        buttonContainer.setOnClickListener(v -> showDynamicFragment(new ContainerFragment()));



        staticFragmentContainerView = findViewById(R.id.static_fragment_container);
        dynamicFragmentContainer = findViewById(R.id.dynamicFragmentContainer);

        fragmentManager = getSupportFragmentManager();

        // Получаем ссылку на статически добавленный фрагмент
        // Важно: Делать это лучше после того как Activity полностью создана,
        // но для простоты можно и здесь, если он точно есть в макете.
        // Безопаснее делать это в onResume или использовать FragmentFactory, но усложнит код.
        staticFragment = fragmentManager.findFragmentById(R.id.static_fragment_container);




        // Настройка лаунчера для ThirdActivity (без изменений)
         // Ваш код лаунчера здесь

        // Обработчики нажатий на кнопки фрагментов
        buttonStatic.setOnClickListener(v -> showStaticFragment());
        buttonDynamic.setOnClickListener(v -> showDynamicFragment(new DynamicFragment()));
        buttonContainer.setOnClickListener(v -> showDynamicFragment(new ContainerFragment())); // Используем ContainerFragment



        // Начальное состояние: показываем статический фрагмент
        if (savedInstanceState == null) {
            showStaticFragment();
        }
        // Если активность пересоздается, Android сам восстановит видимость
        // и состояние фрагментов, если они были добавлены правильно.
    }

    // Метод для ПОКАЗА статического фрагмента
    private void showStaticFragment() {
        if (staticFragment == null) return;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(staticFragment);
        transaction.commit();
        dynamicFragmentContainer.setVisibility(View.GONE);

        Fragment currentDynamicFragment = fragmentManager.findFragmentById(R.id.dynamicFragmentContainer);
        if (currentDynamicFragment != null) {
            fragmentManager.beginTransaction().remove(currentDynamicFragment).commit();
        }

    }

    private void showDynamicFragment(Fragment fragmentToShow) {
        if (staticFragment != null) {
            FragmentTransaction hideTransaction = fragmentManager.beginTransaction();
            hideTransaction.hide(staticFragment);
            hideTransaction.commit();
            dynamicFragmentContainer.setVisibility(View.VISIBLE);

            FragmentTransaction loadTransaction = fragmentManager.beginTransaction();
            loadTransaction.replace(R.id.dynamicFragmentContainer, fragmentToShow);
            loadTransaction.commit();


        }
    }

}

