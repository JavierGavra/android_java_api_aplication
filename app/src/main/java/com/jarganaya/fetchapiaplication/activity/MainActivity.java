package com.jarganaya.fetchapiaplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jarganaya.fetchapiaplication.R;
import com.jarganaya.fetchapiaplication.adapter.MovieListAdapter;
import com.jarganaya.fetchapiaplication.model.MovieListModel;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private final ArrayList<MovieListModel> popularMovieList = new ArrayList<>();
    private final ArrayList<MovieListModel> upcomingMovieList = new ArrayList<>();
    private final ArrayList<MovieListModel> topRatedMovieList = new ArrayList<>();
    private ImageButton ibFavoritePage;
    private RecyclerView rvPopularMovie, rvUpcomingMovie, rvTopRatedMovie;
    private RelativeLayout loadingScreen;
    private RequestQueue requestQueue;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        getApi();

        ibFavoritePage.setOnClickListener(v -> {
            Intent favoriteIntent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(favoriteIntent);
        });
    }

    void initComponents() {
        ibFavoritePage = findViewById(R.id.ib_favorite_page);
        rvPopularMovie = findViewById(R.id.rv_popular_movie);
        rvUpcomingMovie = findViewById(R.id.rv_upcoming_movie);
        rvTopRatedMovie = findViewById(R.id.rv_top_rated_movie);
        loadingScreen = findViewById(R.id.loading_screen);
    }

    void getApi() {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        loadingScreen.setVisibility(View.VISIBLE);
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            popularMovieList.add(gson.fromJson(jsonArray.getString(i), MovieListModel.class));
                        }
                        secondRequest();
                    } catch (Exception e) {
                        handleErrorResponse();
                    }
                },
                error -> handleErrorResponse()
        );
        requestQueue.add(jsonObjectRequest);
    }

    void secondRequest() {
        String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            upcomingMovieList.add(gson.fromJson(jsonArray.getString(i), MovieListModel.class));
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
        String url = "https://api.themoviedb.org/3/movie/top_rated?api_key=57ca7b15d5117ce1e2f3ca7e317f1e80";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        loadingScreen.setVisibility(View.GONE);
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            topRatedMovieList.add(gson.fromJson(jsonArray.getString(i), MovieListModel.class));
                        }
                        setComponentFromResponse();
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
        MovieListAdapter popularMovieListAdapter = new MovieListAdapter(popularMovieList);
        MovieListAdapter upcomingMovieListAdapter = new MovieListAdapter(upcomingMovieList);
        MovieListAdapter topRatedMovieListAdapter = new MovieListAdapter(topRatedMovieList);
        popularMovieListAdapter.setOnItemClickCallback(this::showDetail);
        upcomingMovieListAdapter.setOnItemClickCallback(this::showDetail);
        topRatedMovieListAdapter.setOnItemClickCallback(this::showDetail);

        rvPopularMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvUpcomingMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTopRatedMovie.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularMovie.setAdapter(popularMovieListAdapter);
        rvUpcomingMovie.setAdapter(upcomingMovieListAdapter);
        rvTopRatedMovie.setAdapter(topRatedMovieListAdapter);
    }

    void showDetail(MovieListModel movie) {
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(detailIntent);
    }
}