package com.example.platonov.ui.profile;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.platonov.LoginActivity;
import com.example.platonov.databinding.FragmentProfileBinding;

/**
 * Фрагмент экрана «Профиль».
 * Позволяет пользователю выйти (очистить БД и вернуться на LoginActivity).
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    public ProfileFragment() {
        // Пустой конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            String text = (email == null || email.isEmpty())
                    ? "User: Unknown"
                    : "User: " + email;
            binding.textViewProfileInfo.setText(text);
        });

        binding.buttonLogout.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}