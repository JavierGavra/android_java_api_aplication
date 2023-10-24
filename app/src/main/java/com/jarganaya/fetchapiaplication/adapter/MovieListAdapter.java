package com.jarganaya.fetchapiaplication.adapter;

import android.util.Log;
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

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private ArrayList<MovieListModel> movieListModels;
    private OnItemClickCallback onItemClickCallback;

    public MovieListAdapter(ArrayList<MovieListModel> movieListModels) {
        this.movieListModels = movieListModels;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public MovieListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rectangle_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.ViewHolder holder, int position) {
        MovieListModel movieListModel = movieListModels.get(position);

        Log.d("MovieModel", movieListModel.toString());
        Glide.with(holder.itemView.getContext())
                .load("https://themoviedb.org/t/p/w500" + movieListModel.getPoster_path())
                .placeholder(R.drawable.image_placeholder)
                .into(holder.ivPoster);
        holder.tvTitle.setText(movieListModel.getTitle());
        holder.tvReleaseDate.setText(movieListModel.getRelease_date());
        holder.tvRating.setText(Double.toString(movieListModel.getVote_average()));

        holder.itemView.setOnClickListener(v -> onItemClickCallback.onItemClick(movieListModels.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return movieListModels.size();
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
        void onItemClick(MovieListModel movieListModel);
    }
}
