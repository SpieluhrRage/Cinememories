package com.example.platonov.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.platonov.data.prefs.SessionManager;

public class ProfileViewModel extends AndroidViewModel {

    private final SessionManager session;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        session = new SessionManager(application);
    }

    public void logout() {
        session.logout();
    }
}
