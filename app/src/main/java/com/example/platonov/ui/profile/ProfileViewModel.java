package com.example.platonov.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.platonov.data.dao.UserDao;
import com.example.platonov.data.database.AppDatabase;
import com.example.platonov.data.prefs.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {

    private final SessionManager session;
    private final UserDao userDao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> userEmail = new MutableLiveData<>(null);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        // Инициализируем БД и DAO ЗДЕСЬ
        AppDatabase db = AppDatabase.getInstance(application);
        this.userDao = (db != null) ? db.userDao() : null;
        this.session = new SessionManager(application);

        loadEmail();
    }

    private void loadEmail() {
        final long uid = session.getUserId();
        if (uid <= 0 || userDao == null) {
            userEmail.postValue(null);
            return;
        }
        io.execute(() -> {
            String email = userDao.getEmailById(uid); // синхронный запрос в бэкграунде
            userEmail.postValue(email);
        });
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public void logout() {
        session.logout();
        // (опционально) чистить БД не обязательно; если надо — добавь здесь
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        io.shutdown();
    }
}
