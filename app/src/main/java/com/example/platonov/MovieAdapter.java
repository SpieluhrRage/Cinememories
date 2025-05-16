package com.example.platonov; // Убедитесь, что пакет правильный

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context; // Контекст нужен для LayoutInflater и, возможно, для загрузки ресурсов
    private OnItemClickListener listener; // Интерфейс для обработки кликов

    // Интерфейс для слушателя кликов
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    // Конструктор
    public MovieAdapter(Context context, List<Movie> movieList, OnItemClickListener listener) {
        this.context = context;
        // Создаем копию списка, чтобы избежать проблем с модификацией оригинального списка извне
        this.movieList = new ArrayList<>(movieList);
        this.listener = listener;
    }

    // Альтернативный конструктор, если слушатель не нужен (хотя в данном случае он нужен)
    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = new ArrayList<>(movieList);
        this.listener = null; // Нет слушателя по умолчанию
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Используем context, который был передан в конструкторе
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        // Убедитесь, что у вас есть list_item_movie.xml с правильными ID
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = movieList.get(position);
        holder.bind(currentMovie, listener); // Передаем фильм и слушатель в ViewHolder
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Метод для обновления всего списка фильмов
    public void updateMovies(List<Movie> newMovies) {
        movieList.clear();
        if (newMovies != null) {
            movieList.addAll(newMovies);
        }
        notifyDataSetChanged(); // Уведомляем адаптер об изменении данных
    }

    // ViewHolder
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewGenre;
        // Добавьте другие View, если они есть в вашем list_item_movie.xml

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Убедитесь, что ID в R.layout.list_item_movie соответствуют этим
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster); // Пример ID
            textViewTitle = itemView.findViewById(R.id.textViewTitle);     // Пример ID
            textViewGenre = itemView.findViewById(R.id.textViewGenre);     // Пример ID
        }

        // Метод для привязки данных к ViewHolder
        public void bind(final Movie movie, final OnItemClickListener listener) {
            textViewTitle.setText(movie.getTitle());
            textViewGenre.setText(movie.getGenre() != null ? movie.getGenre() : "Жанр не указан");

            String posterUriString = movie.getPosterUri();
            Uri posterUri = null;
            if (posterUriString != null && !posterUriString.isEmpty()) {
                try {
                    posterUri = Uri.parse(posterUriString);
                } catch (Exception e) {
                    Log.e("MovieAdapter", "Error parsing URI: " + posterUriString, e);
                }
            }

            if (posterUri != null) {
                try {
                    // Попытка установить URI напрямую.
                    // Для content:// URI это может быть медленно и требовать обработки в фоновом потоке
                    // или использования библиотек типа Glide/Picasso для эффективной загрузки.
                    // Для android.resource:// обычно работает нормально.
                    imageViewPoster.setImageURI(posterUri);

                    // Проверка, существует ли файл по URI (особенно для content://)
                    // Это может быть полезно для отладки, но не решает проблему производительности
                    if (ContentResolver.SCHEME_CONTENT.equals(posterUri.getScheme())) {
                        try {
                            itemView.getContext().getContentResolver().openInputStream(posterUri).close();
                        } catch (Exception e) {
                            Log.w("MovieAdapter", "Cannot open content URI: " + posterUri, e);
                            imageViewPoster.setImageResource(R.drawable.poster); // Заглушка
                        }
                    }

                } catch (SecurityException se) {
                    Log.e("MovieAdapter", "SecurityException for URI: " + posterUri, se);
                    imageViewPoster.setImageResource(R.drawable.poster); // Заглушка
                } catch (OutOfMemoryError oom) {
                    Log.e("MovieAdapter", "OutOfMemoryError for URI: " + posterUri, oom);
                    imageViewPoster.setImageResource(R.drawable.poster); // Заглушка
                    // Рассмотрите уменьшение размера изображений или использование библиотек
                }
                catch (Exception e) {
                    Log.e("MovieAdapter", "Generic exception for URI: " + posterUri, e);
                    imageViewPoster.setImageResource(R.drawable.poster); // Заглушка
                }
            } else {
                imageViewPoster.setImageResource(R.drawable.poster); // Заглушка по умолчанию
            }

            // Устанавливаем слушатель клика на весь элемент списка
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition(); // Получаем актуальную позицию
                    if (position != RecyclerView.NO_POSITION) { // Проверка, что позиция валидна
                        listener.onItemClick(movie);
                    }
                }
            });
        }
    }
}