package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.models.MovieTheater;
import java.util.ArrayList;
import java.util.List;

public class MovieTheaterSelectionActivity extends AppCompatActivity {

    // Variables to control XML items
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_theater_selection);

        // Variables assignment to control XML items
        recyclerView = findViewById(R.id.recyclerViewMovieTheater);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Information get from previous activity
        Bundle bundle = getIntent().getExtras();
        String clientID = bundle.getString("clientID");

        getMovieTheatersInformation(clientID);

    }

    // Get the movie theaters information from the SQLite Data Base to load it into the XML items
    public void getMovieTheatersInformation(String clientID) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        // Getting movie theater
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

                String selectedMovieTheater = movieTheaterList.get(recyclerView.getChildAdapterPosition(view)).getName();

                openMovieSelectionActivity(clientID, selectedMovieTheater);

            }
        });

        recyclerView.setAdapter(movieTheaterAdapter);

    }

    // Opens the activity where the user can select the movie and sends the previous information selected
    private void openMovieSelectionActivity(String clientID, String selectedMovieTheater) {

        Intent intent = new Intent(this, MovieSelectionActivity.class);

        intent.putExtra("clientID", clientID);
        intent.putExtra("selectedMovieTheater", selectedMovieTheater);

        startActivity(intent);

    }

}