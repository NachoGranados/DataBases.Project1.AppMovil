package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.interfaces.MovieTheaterRestAPI;
import com.example.cinetec.models.MovieTheater;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieTheaterSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_theater_selection);

        recyclerView = findViewById(R.id.recyclerViewMovieTheater);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMovieTheatersInformation();

    }

    public void getMovieTheatersInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                 .addConverterFactory(GsonConverterFactory.create()).build();

        MovieTheaterRestAPI movieTheaterRestAPI = retrofit.create(MovieTheaterRestAPI.class);

        Call<List<MovieTheater>> call = movieTheaterRestAPI.getMovieTheaters();
        call.enqueue(new Callback<List<MovieTheater>>() {

            @Override
            public void onResponse(Call<List<MovieTheater>> call, Response<List<MovieTheater>> response) {

                try {

                    if (response.isSuccessful()) {

                        //Toast.makeText(MovieTheaterSelectionActivity.this, "Successful Movie Theaters GET", Toast.LENGTH_SHORT).show();

                        List<MovieTheater> movieTheaterList = response.body();

                        MovieTheaterAdapter movieTheaterAdapter = new MovieTheaterAdapter(MovieTheaterSelectionActivity.this, movieTheaterList);

                        recyclerView.setAdapter(movieTheaterAdapter);

                    } else {

                        //Toast.makeText(MovieTheaterSelectionActivity.this, "Unsuccessful Movie Theaters GET", Toast.LENGTH_SHORT).show();

                        return;

                    }

                } catch (Exception exception) {

                    Toast.makeText(MovieTheaterSelectionActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<MovieTheater>> call, Throwable t) {

                Toast.makeText(MovieTheaterSelectionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}