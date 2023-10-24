package com.jarganaya.fetchapiaplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jarganaya.fetchapiaplication.R;
import com.jarganaya.fetchapiaplication.adapter.MovieListAdapter;
import com.jarganaya.fetchapiaplication.model.MovieDetailModel;
import com.jarganaya.fetchapiaplication.model.MovieListModel;
import com.jarganaya.fetchapiaplication.model.MovieVideoModel;
import com.jarganaya.fetchapiaplication.room.AppDatabase;
import com.jarganaya.fetchapiaplication.room.entity.FavoriteMovieEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    public static String EXTRA_MOVIE_ID = "extra_movie_id";

    private MovieDetailModel movieDetailModel;
    private MovieVideoModel movieVideoModel;
    private final ArrayList<MovieListModel> similarMovieList = new ArrayList<>();
    private AppDatabase db;
    private RelativeLayout loadingScreen;
    private ImageView ivBackButton, ivFavoriteButton, ivPlayTrailerButton, ivBackdrop;
    private TextView tvTitle, tvOriginalTitle, tvRuntime, tvVoteAverage, tvReleaseDate, tvStatus, tvSynopsis;
    private RecyclerView rvSimilarMovie;
    private RequestQueue requestQueue;
    private boolean isFavorite = false;
    private final Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_detail);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").allowMainThreadQueries().build();
        initComponents();
        getApi();


        ivBackButton.setOnClickListener(v -> finish());

        ivFavoriteButton.setOnClickListener(v -> setFavorite());

        ivPlayTrailerButton.setOnClickListener(v -> {
            if (movieVideoModel == null) {
                Toast.makeText(this, "No trailer in this movie", Toast.LENGTH_SHORT).show();
            } else {
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/" + movieVideoModel.getKey()));
                startActivity(trailerIntent);
            }
        });
    }

    void initComponents() {
        ivBackButton = findViewById(R.id.iv_back_button);
        ivFavoriteButton = findViewById(R.id.iv_favorite_button);
        ivPlayTrailerButton = findViewById(R.id.iv_play_trailer_button);
        ivBackdrop = findViewById(R.id.iv_backdrop);
        tvTitle = findViewById(R.id.tv_title);
        tvOriginalTitle = findViewById(R.id.tv_original_title);
        tvRuntime = findViewById(R.id.tv_runtime);
        tvVoteAverage = findViewById(R.id.tv_vote_average);
        tvReleaseDate = findViewById(R.id.tv_release_date);
        tvStatus = findViewById(R.id.tv_status);
        tvSynopsis = findViewById(R.id.tv_synopsis);
        loadingScreen = findViewById(R.id.loading_screen);
        rvSimilarMovie = findViewById(R.id.rv_similar_movie);
    }

    void getApi() {
        String url = "https://api.themoviedb.org/3/movie/"+getIntent().getLongExtra(EXTRA_MOVIE_ID, 575264)+"?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        loadingScreen.setVisibility(View.VISIBLE);
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    movieDetailModel = gson.fromJson(response.toString(), MovieDetailModel.class);
                    secondRequest();
                },
                error -> handleErrorResponse()
        );
        requestQueue.add(jsonObjectRequest);
    }

    void secondRequest() {
        String url = "https://api.themoviedb.org/3/movie/"+getIntent().getLongExtra(EXTRA_MOVIE_ID, 575264)+"/videos?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (jsonArray.getJSONObject(i).getString("type").equals("Trailer")) {
                                movieVideoModel = gson.fromJson(jsonArray.getString(i), MovieVideoModel.class);
                                break;
                            }
                        }
                        thirdRequest();
                    } catch (Exception e) {
                        handleErrorResponse();
                    }
                },
                error -> handleErrorResponse()
        );
        requestQueue.add(jsonObjectRequest);
    }

    void thirdRequest() {
        String url = "https://api.themoviedb.org/3/movie/"+getIntent().getLongExtra(EXTRA_MOVIE_ID, 575264)+"/similar?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            similarMovieList.add(gson.fromJson(jsonArray.getString(i), MovieListModel.class));
                        }
                        setComponentFromResponse();
                        loadingScreen.setVisibility(View.GONE);
                    } catch (Exception e) {
                        handleErrorResponse();
                    }
                },
                error -> handleErrorResponse()
        );
        requestQueue.add(jsonObjectRequest);
    }

    void handleErrorResponse() {
        Toast.makeText(this, "Error when fetch API", Toast.LENGTH_SHORT).show();
    }

    void setComponentFromResponse() {
        Glide.with(this)
                .load("https://themoviedb.org/t/p/w500" + movieDetailModel.getBackdrop_path())
                .placeholder(R.drawable.image_placeholder)
                .into(ivBackdrop);
        tvTitle.setText(movieDetailModel.getTitle());
        tvOriginalTitle.setText(movieDetailModel.getOriginal_title());
        tvRuntime.setText(Float.toString(movieDetailModel.getRuntime()));
        tvVoteAverage.setText(Float.toString(movieDetailModel.getVote_average()));
        tvReleaseDate.setText(movieDetailModel.getRelease_date());
        tvStatus.setText(movieDetailModel.getStatus());
        tvSynopsis.setText(movieDetailModel.getOverview());

        // Cek apakah film sudah ada di favorit
        ArrayList<FavoriteMovieEntity> favoriteMovieEntities = (ArrayList<FavoriteMovieEntity>) db.favoriteMovieDao().getAll();
        for (int i = 0; i < favoriteMovieEntities.size(); i++) {
            if (favoriteMovieEntities.get(i).movieId == movieDetailModel.getId()) {
                isFavorite = true;
                ivFavoriteButton.setImageResource(R.drawable.ic_detail_favorite);
                break;
            }
        }

        MovieListAdapter similarMovieListAdapter = new MovieListAdapter(similarMovieList);
        similarMovieListAdapter.setOnItemClickCallback(this::showDetail);
        rvSimilarMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSimilarMovie.setAdapter(similarMovieListAdapter);
    }

    void showDetail(MovieListModel movie) {
        Intent detailIntent = new Intent(DetailActivity.this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(detailIntent);
    }

    void setFavorite() {
        isFavorite = !isFavorite;
        if (isFavorite) {
            FavoriteMovieEntity favoriteMovie = new FavoriteMovieEntity(
                    (int) movieDetailModel.getId(),
                    movieDetailModel.getTitle(),
                    movieDetailModel.getPoster_path(),
                    movieDetailModel.getRelease_date(),
                    Float.toString(movieDetailModel.getVote_average())
            );
            db.favoriteMovieDao().insert(favoriteMovie);
        } else {
            db.favoriteMovieDao().delete((int)movieDetailModel.getId());
        }

        ivFavoriteButton.setImageResource(isFavorite? R.drawable.ic_detail_favorite : R.drawable.ic_detail_favorite_border);
        Toast.makeText(this, isFavorite? "Success add to favorite" : "Remove from favorite", Toast.LENGTH_SHORT).show();
        Log.d("App Database", "data : " + db.favoriteMovieDao().getAll().size());
    }
}