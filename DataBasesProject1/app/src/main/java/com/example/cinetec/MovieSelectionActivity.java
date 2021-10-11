package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cinetec.adapters.MovieAdapter;
import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.MovieTheater;

import java.util.ArrayList;
import java.util.List;

public class MovieSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_selection);

        recyclerView = findViewById(R.id.recyclerViewMovie);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();

        String selectedMovieTheater = bundle.getString("selectedMovieTheater");

        getMoviesInformation();

    }

    private void getMoviesInformation() {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MOVIE", null);

        List<Movie> movieList = new ArrayList<>();

        while(cursor.moveToNext()) {

            String originalName = cursor.getString(0);
            String gendre = cursor.getString(1);
            String name = cursor.getString(2);
            String director = cursor.getString(3);
            String imageUrl = cursor.getString(4);
            String lenght = cursor.getString(5);

            Movie movie = new Movie();

            movie.setOriginalName(originalName);
            movie.setGendre(gendre);
            movie.setName(name);
            movie.setDirector(director);
            movie.setImageUrl(imageUrl);
            movie.setLenght(Integer.parseInt(lenght));

            movieList.add(movie);

        }

        MovieAdapter movieAdapter = new MovieAdapter(MovieSelectionActivity.this, movieList);

        movieAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MovieSelectionActivity.this,"Selection = " + movieList.get(recyclerView.getChildAdapterPosition(view)).getName(), Toast.LENGTH_SHORT).show();

            }
        });

        recyclerView.setAdapter(movieAdapter);

    }


}