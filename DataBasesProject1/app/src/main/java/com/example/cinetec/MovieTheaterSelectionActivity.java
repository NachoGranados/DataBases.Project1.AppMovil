package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.models.MovieTheater;

import java.util.ArrayList;
import java.util.List;

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

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MOVIE_THEATER", null);

        List<MovieTheater> movieTheaterList = new ArrayList<>();

        while(cursor.moveToNext()) {

            String name = cursor.getString(0);
            String location = cursor.getString(1);
            String cinemaAmount = cursor.getString(2);

            MovieTheater movieTheater = new MovieTheater();

            movieTheater.setName(name);
            movieTheater.setLocation(location);
            movieTheater.setCinemaAmount(Integer.parseInt(cinemaAmount));

            movieTheaterList.add(movieTheater);

        }

        MovieTheaterAdapter movieTheaterAdapter = new MovieTheaterAdapter(MovieTheaterSelectionActivity.this, movieTheaterList);

        movieTheaterAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MovieTheaterSelectionActivity.this,"Selection = " + movieTheaterList.get(recyclerView.getChildAdapterPosition(view)).getName(), Toast.LENGTH_SHORT).show();

            }
        });

        recyclerView.setAdapter(movieTheaterAdapter);

    }

}