package com.example.platonov; // Замени на свой пакет

import android.app.Activity;
import android.content.ContentResolver; // Для проверки схемы URI
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources; // Для обработки ошибок ресурсов
import android.net.Uri;
import android.util.Log; // Для логирования ошибок
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// УБИРАЕМ ИМПОРТ GLIDE
// import com.bumptech.glide.Glide;

import java.io.FileNotFoundException; // Для обработки ошибок URI
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private final LayoutInflater inflater;

    // Конструктор
    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.movieList = new ArrayList<>(movieList);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = movieList.get(position);

        holder.textViewTitle.setText(currentMovie.getTitle());
        holder.textViewGenre.setText(currentMovie.getGenre() != null ? currentMovie.getGenre() : "Жанр не указан");

        // --- Загрузка изображения БЕЗ Glide ---
        String posterUriString = currentMovie.getPosterUri();
        Uri posterUri = null;
        if (posterUriString != null && !posterUriString.isEmpty()) {
            try {
                posterUri = Uri.parse(posterUriString);
            } catch (Exception e) {
                Log.e("MovieAdapter", "Error parsing URI: " + posterUriString, e);
                posterUri = null; // Сбрасываем URI в случае ошибки парсинга
            }
        }

        if (posterUri != null) {
            try {
                // Пытаемся установить URI напрямую.
                // ВАЖНО: Для content:// URI это может быть медленно в RecyclerView!
                holder.imageViewPoster.setImageURI(posterUri);

                // Дополнительная проверка для content:// URI (не гарантирует быструю загрузку):
                // Иногда setImageURI не срабатывает сразу или выдает ошибку без исключения,
                // можно попробовать открыть поток, чтобы убедиться, что URI валиден,
                // но это не решает проблему производительности.
                 /*
                 if (ContentResolver.SCHEME_CONTENT.equals(posterUri.getScheme())) {
                     InputStream inputStream = context.getContentResolver().openInputStream(posterUri);
                     if (inputStream != null) {
                          holder.imageViewPoster.setImageURI(posterUri);
                          inputStream.close(); // Закрываем поток
                     } else {
                          // Если поток null, URI недействителен или нет прав
                          Log.w("MovieAdapter", "Could not open InputStream for URI: " + posterUri);
                          holder.imageViewPoster.setImageResource(R.drawable.poster); // Ставим заглушку
                     }
                 } else {
                     // Для android.resource:// и file:// обычно setImageURI работает нормально
                     holder.imageViewPoster.setImageURI(posterUri);
                 }
                 */

            } catch (SecurityException se) {
                // Ошибка прав доступа к URI (особенно для content://)
                Log.e("MovieAdapter", "SecurityException loading URI: " + posterUri, se);
                holder.imageViewPoster.setImageResource(R.drawable.poster); // Ставим заглушку
            } catch (Exception e) {
                // Другие возможные ошибки при работе с URI
                Log.e("MovieAdapter", "Exception loading URI: " + posterUri, e);
                holder.imageViewPoster.setImageResource(R.drawable.poster); // Ставим заглушку
            }
        } else {
            // Если URI нет (null или пустой), ставим заглушку
            holder.imageViewPoster.setImageResource(R.drawable.poster);
        }
        // --- Конец загрузки изображения БЕЗ Glide ---

        // Обработчик клика на элемент
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Movie clickedMovie = movieList.get(currentPosition);
                    Intent intent = new Intent(context, FilmDetails.class); // Используй имя FilmDetails
                    intent.putExtra("movie", clickedMovie);

                    // --- Изменение здесь: Запускаем для получения результата ---
                    // Определяем код запроса (лучше вынести в константы класса Fragment или Activity)
                    final int VIEW_MOVIE_DETAILS_REQUEST = 1001;

                    // Проверяем, что context является Activity или получаем Activity из Fragment'а
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, VIEW_MOVIE_DETAILS_REQUEST);
                    } else {
                        // Если адаптер используется во Фрагменте, нужен другой подход:
                        // Либо передавать Fragment в конструктор адаптера, либо использовать интерфейс-callback
                        // Простой вариант (менее надежный): попытка получить Activity из View
                        View rootView = holder.itemView.getRootView();
                        if (rootView.getContext() instanceof Activity) {
                            ((Activity) rootView.getContext()).startActivityForResult(intent, VIEW_MOVIE_DETAILS_REQUEST);
                        } else {
                            // Не удалось запустить для результата стандартным способом
                            Log.e("MovieAdapter", "Cannot start Activity for result from this context");
                            context.startActivity(intent); // Запускаем без ожидания результата как запасной вариант
                        }
                    }
                }
                else {
                    Log.e("MovieAdapter", "Clicked movie is null at position: " + currentPosition);
                    Toast.makeText(context, "Ошибка: Фильм не найден", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Метод для обновления всего списка
    public void updateMovies(List<Movie> newMovies) {
        movieList.clear();
        if (newMovies != null) {
            movieList.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    // Метод для добавления одного фильма
    public void addMovie(Movie movie) {
        if (movie != null) {
            movieList.add(movie);
            notifyItemInserted(movieList.size() - 1);
        }
    }

    // ViewHolder (остается без изменений)
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewGenre;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
        }
    }
}