package com.example.platonov.ui.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.platonov.R;
import com.example.platonov.data.entity.MovieEntity;

/**
 * Адаптер для RecyclerView, отображает список MovieEntity.
 * ListAdapter использует DiffUtil для эффективного обновления списков.
 */
public class MovieListAdapter extends ListAdapter<MovieEntity, MovieListAdapter.MovieViewHolder> {

    /**
     * Интерфейс для обратного вызова при клике на элемент списка,
     * на кнопку "избранное" или на кнопку "удалить".
     */
    public interface OnItemClickListener {
        void onItemClick(MovieEntity movie);
        void onFavoriteClick(MovieEntity movie);
        void onDeleteClick(MovieEntity movie);   // ← здесь добавили метод для корзины
    }

    private final OnItemClickListener listener;

    public MovieListAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // Указываем, как сравнивать элементы (DiffUtil.ItemCallback)
    private static final DiffUtil.ItemCallback<MovieEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MovieEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull MovieEntity oldItem, @NonNull MovieEntity newItem) {
                    // Сравниваем ID (единственный признак уникального фильма)
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull MovieEntity oldItem, @NonNull MovieEntity newItem) {
                    // Сравниваем все поля на предмет изменений
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getYear() == newItem.getYear()
                            && oldItem.getGenre().equals(newItem.getGenre())
                            && oldItem.isFavorite() == newItem.isFavorite();
                    // Можно добавить проверку posterUrl, synopsis и т. д., если нужно
                }
            };

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Инфлейтим макет item_movie.xml
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Получаем текущий объект MovieEntity
        MovieEntity currentMovie = getItem(position);
        // Передаем его во ViewHolder вместе с listener-ом
        holder.bind(currentMovie, listener);
    }

    /**
     * ViewHolder, содержащий логику привязки данных к элементам View.
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewPoster;
        private final TextView textViewTitle;
        private final TextView textViewYear;
        private final TextView textViewGenre;
        private final ImageButton buttonFavorite;
        private final ImageButton buttonDelete;  // ← добавили поле для корзины

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            textViewGenre = itemView.findViewById(R.id.textViewGenres);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);  // ← ищем корзину по ID
        }

        /**
         * Привязываем данные фильма к View:
         * - Заполняем TextView (title, year, genre)
         * - Загружаем постер через Glide
         * - Устанавливаем корректную иконку звёздочки и навешиваем onFavoriteClick
         * - Устанавливаем onDeleteClick по кнопке корзины
         * - Слушаем клик по самому элементу для перехода к деталям
         */
        public void bind(final MovieEntity movie, final OnItemClickListener listener) {
            // 1) Title
            textViewTitle.setText(movie.getTitle());

            // 2) Year
            textViewYear.setText(String.valueOf(movie.getYear()));

            // 3) Genre (строка "Action, Drama", либо пустая)
            textViewGenre.setText(movie.getGenre() != null ? movie.getGenre() : "");

            // 4) Poster (через Glide)
            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                Glide.with(imageViewPoster.getContext())
                        .load(movie.getPosterUrl())
                        .placeholder(R.drawable.ic_placeholder)
                        .into(imageViewPoster);
            } else {
                imageViewPoster.setImageResource(R.drawable.ic_placeholder);
            }

            // 5) Favorite (звёздочка)
            if (movie.isFavorite()) {
                // иконка "звезда заполнена"
                buttonFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                // иконка "звезда контур"
                buttonFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            }
            buttonFavorite.setOnClickListener(v -> {
                listener.onFavoriteClick(movie);
            });

            // 6) Delete (корзина)
            buttonDelete.setOnClickListener(v -> {
                listener.onDeleteClick(movie);
            });

            // 7) Открыть детали при клике на весь элемент
            itemView.setOnClickListener(v -> {
                listener.onItemClick(movie);
            });
        }
    }
}
