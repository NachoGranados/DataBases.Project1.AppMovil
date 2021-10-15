package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.cinetec.adapters.ConfirmationSeatAdapter;
import com.example.cinetec.adapters.MovieAdapter;
import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.adapters.ScreeningAdapter;
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.MovieTheater;
import com.example.cinetec.models.Screening;
import com.example.cinetec.models.Seat;
import java.util.ArrayList;
import java.util.List;

public class ConfirmationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMovieTheater;
    private RecyclerView recyclerViewMovie;
    private RecyclerView recyclerViewScreening;
    private RecyclerView recyclerViewSeat;

    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        recyclerViewMovieTheater = findViewById(R.id.recyclerViewMovieTheaterConfirmation);
        recyclerViewMovieTheater.setHasFixedSize(true);
        recyclerViewMovieTheater.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewMovie = findViewById(R.id.recyclerViewMovieConfirmation);
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewScreening = findViewById(R.id.recyclerViewSreeningConfirmation);
        recyclerViewScreening.setHasFixedSize(true);
        recyclerViewScreening.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewSeat = findViewById(R.id.recyclerViewSeatConfirmation);
        recyclerViewSeat.setHasFixedSize(true);
        recyclerViewSeat.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();

        String selectedMovieTheater = bundle.getString("selectedMovieTheater");
        String selectedMovieOriginalName = bundle.getString("selectedMovieOriginalName");
        String selectedScreeningId = bundle.getString("selectedScreeningId");
        String selectedMovieImageURL = bundle.getString("selectedMovieImageURL");

        List<Seat> seatList = SeatSelectionActivity.getSelectedSeatList();


        continueButton = findViewById(R.id.buttonConfirmation);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSeatInformation(selectedScreeningId, seatList);

                openMovieTheaterSelectionActivity();

            }

        });

        getConfirmationInformation(selectedMovieTheater, selectedMovieOriginalName, selectedScreeningId, selectedMovieImageURL, seatList);

    }

    private void getConfirmationInformation(String selectedMovieTheater, String selectedMovieOriginalName, String selectedScreeningId, String selectedMovieImageURL, List<Seat> seatList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM MOVIE_THEATER WHERE Name ='" + selectedMovieTheater + "'", null);

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

        MovieTheaterAdapter movieTheaterAdapter = new MovieTheaterAdapter(ConfirmationActivity.this, movieTheaterList);

        recyclerViewMovieTheater.setAdapter(movieTheaterAdapter);

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM MOVIE WHERE Original_name ='" + selectedMovieOriginalName + "'", null);

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

        MovieAdapter movieAdapter = new MovieAdapter(ConfirmationActivity.this, movieList);

        recyclerViewMovie.setAdapter(movieAdapter);

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM SCREENING WHERE ID =" + selectedScreeningId,null);

        List<Screening> screeningList = new ArrayList<>();

        while(cursor.moveToNext()) {

            String id = cursor.getString(0);
            String cinemaNumber = cursor.getString(1);
            String movieOriginalName = cursor.getString(2);
            String hour = cursor.getString(3);
            String capacity = cursor.getString(4);

            Screening screening = new Screening();

            screening.setId(Integer.parseInt(id));
            screening.setCinemaNumber(Integer.parseInt(cinemaNumber));
            screening.setMovieOriginalName(movieOriginalName);
            screening.setHour(Integer.parseInt(hour));
            screening.setCapacity(Integer.parseInt(capacity));

            screeningList.add(screening);

        }

        ScreeningAdapter screeningAdapter = new ScreeningAdapter(ConfirmationActivity.this, screeningList, selectedMovieImageURL);

        recyclerViewScreening.setAdapter(screeningAdapter);

        ConfirmationSeatAdapter ConfirmationSeatAdapter = new ConfirmationSeatAdapter(ConfirmationActivity.this, seatList);

        recyclerViewSeat.setAdapter(ConfirmationSeatAdapter);

    }

    private void openMovieTheaterSelectionActivity() {

        Intent intent = new Intent(this, MovieTheaterSelectionActivity.class);
        startActivity(intent);

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

    private void updateSeatInformation(String selectedScreeningId, List<Seat> seatList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < seatList.size(); i++) {

            Seat seat = seatList.get(i);

            sqLiteDatabase.execSQL("DELETE FROM SEAT WHERE Screening_id=" + seat.getScreeningId() + " AND Row_num=" + seat.getRowNum() + " AND Column_num=" + seat.getColumnNum());

            ContentValues contentValues = new ContentValues();

            contentValues.put("Screening_id", seat.getScreeningId());
            contentValues.put("Row_num", seat.getRowNum());
            contentValues.put("Column_num", seat.getColumnNum());
            contentValues.put("State", "sold");

            if(checkInternetConnection()) {

                contentValues.put("Sync_status", 1);

            } else {

                contentValues.put("Sync_status", 0);

            }

            sqLiteDatabase.insert("SEAT", null, contentValues);

        }

        Toast.makeText(ConfirmationActivity.this, "Successful purchase", Toast.LENGTH_SHORT).show();

        sqLiteDatabase.close();

    }

}
