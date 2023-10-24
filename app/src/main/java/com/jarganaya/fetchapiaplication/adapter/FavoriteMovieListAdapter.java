package com.jarganaya.fetchapiaplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jarganaya.fetchapiaplication.R;
import com.jarganaya.fetchapiaplication.model.MovieListModel;
import com.jarganaya.fetchapiaplication.room.entity.FavoriteMovieEntity;

import java.util.ArrayList;

public class FavoriteMovieListAdapter extends RecyclerView.Adapter<FavoriteMovieListAdapter.ViewHolder> {
    private ArrayList<FavoriteMovieEntity> favoriteMovieEntities;
    private OnItemClickCallback onItemClickCallback;

    public FavoriteMovieListAdapter(ArrayList<FavoriteMovieEntity> favoriteMovieEntities) {
        this.favoriteMovieEntities = favoriteMovieEntities;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public FavoriteMovieListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_horizontal_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMovieListAdapter.ViewHolder holder, int position) {
        FavoriteMovieEntity favoriteMovie = favoriteMovieEntities.get(position);

        Glide.with(holder.itemView.getContext())
                .load("https://themoviedb.org/t/p/w500" + favoriteMovie.posterPath)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.ivPoster);
        holder.tvTitle.setText(favoriteMovie.title);
        holder.tvReleaseDate.setText(favoriteMovie.releaseDate);
        holder.tvRating.setText(favoriteMovie.rating);

        holder.itemView.setOnClickListener(v -> onItemClickCallback.onItemClick(favoriteMovieEntities.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return favoriteMovieEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster;
        private TextView tvTitle, tvReleaseDate, tvRating;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvReleaseDate = itemView.findViewById(R.id.tv_release_date);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(FavoriteMovieEntity favoriteMovie);
    }
}
