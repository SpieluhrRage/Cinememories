package com.example.platonov.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс, который создаёт синглтон-экземпляр Retrofit, настроенный для TMDb.
 */
public class TmdbRetrofitClient {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static TmdbApiService tmdbApiService;

    public static TmdbApiService getInstance() {
        if (tmdbApiService == null) {
            synchronized (TmdbRetrofitClient.class) {
                if (tmdbApiService == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    tmdbApiService = retrofit.create(TmdbApiService.class);
                }
            }
        }
        return tmdbApiService;
    }
}
