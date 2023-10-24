package com.jarganaya.fetchapiaplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jarganaya.fetchapiaplication.R;
import com.jarganaya.fetchapiaplication.adapter.FavoriteMovieListAdapter;
import com.jarganaya.fetchapiaplication.model.MovieListModel;
import com.jarganaya.fetchapiaplication.room.AppDatabase;
import com.jarganaya.fetchapiaplication.room.entity.FavoriteMovieEntity;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private AppDatabase db;
    private ArrayList<FavoriteMovieEntity> favoriteMovies;
    private ImageButton ibBack;
    private RecyclerView rvFavoriteMovie;
    private RelativeLayout loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initComponents();
        getData();

        ibBack.setOnClickListener(v -> finish());
    }

    void initComponents() {
        ibBack = findViewById(R.id.ib_back);
        rvFavoriteMovie = findViewById(R.id.rv_favorite_movie);
        loadingScreen = findViewById(R.id.loading_screen);
    }

    void getData() {
        loadingScreen.setVisibility(View.VISIBLE);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").allowMainThreadQueries().build();
        favoriteMovies = (ArrayList<FavoriteMovieEntity>) db.favoriteMovieDao().getAll();
        setComponentFromResponse();
        loadingScreen.setVisibility(View.GONE);
    }

    void setComponentFromResponse() {
        FavoriteMovieListAdapter favoriteMovieListAdapter = new FavoriteMovieListAdapter(favoriteMovies);
        favoriteMovieListAdapter.setOnItemClickCallback(this::showDetail);
        rvFavoriteMovie.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteMovie.setAdapter(favoriteMovieListAdapter);
    }

    void showDetail(FavoriteMovieEntity favoriteMovie) {
        Intent detailIntent = new Intent(FavoriteActivity.this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_MOVIE_ID, (long)favoriteMovie.movieId);
        startActivity(detailIntent);
    }
}