package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cinetec.adapters.ScreeningAdapter;
import com.example.cinetec.models.Cinema;
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.Screening;
import java.util.ArrayList;
import java.util.List;

public class ScreeningSelectionActivity extends AppCompatActivity {

    // Variables to control XML items
    private RecyclerView recyclerView;
    private TextView movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screening_selection);

        // Variables assignment to control XML items
        recyclerView = findViewById(R.id.recyclerViewScreening);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Information get from previous activity
        Bundle bundle = getIntent().getExtras();
        String clientID = bundle.getString("clientID");
        String selectedMovieTheater = bundle.getString("selectedMovieTheater");
        String selectedMovieOriginalName = bundle.getString("selectedMovieOriginalName");
        String selectedMovieImageURL = bundle.getString("selectedMovieImageURL");

        movieList = findViewById(R.id.textViewScreeningList);

        movieList.setText(selectedMovieTheater + " Screening List");

        getScreeningsInformation(clientID, selectedMovieTheater, selectedMovieOriginalName, selectedMovieImageURL);

    }

    // Get the screenings information from the SQLite Data Base to load it into the XML items
    private void getScreeningsInformation(String clientID, String selectedMovieTheater, String selectedMovieOriginalName, String selectedMovieImageURL) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        // Getting cinema by name of the movie theater
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM CINEMA WHERE Name_movie_theater ='" + selectedMovieTheater + "'", null);

        List<Cinema> cinemaList = new ArrayList<>();

        while(cursor.moveToNext()) {

            String number = cursor.getString(0);
            String rows = cursor.getString(1);
            String columns = cursor.getString(2);
            String capacity = cursor.getString(3);
            String nameMovieTheater = cursor.getString(4);

            Cinema cinema = new Cinema();

            cinema.setNumber(Integer.parseInt(number));
            cinema.setRows(Integer.parseInt(rows));
            cinema.setColumns(Integer.parseInt(columns));
            cinema.setCapacity(Integer.parseInt(capacity));
            cinema.setNameMovieTheater(nameMovieTheater);

            cinemaList.add(cinema);

        }

        List<Screening> screeningList = new ArrayList<>();

        for(int i = 0; i < cinemaList.size(); i++) {

            // Getting screening by cinema number and movie original name
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM SCREENING WHERE Cinema_number =" + cinemaList.get(i).getNumber() + " AND Movie_original_name ='" + selectedMovieOriginalName + "'",null);

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

        }

        ScreeningAdapter screeningAdapter = new ScreeningAdapter(ScreeningSelectionActivity.this, screeningList, selectedMovieImageURL);

        screeningAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedScreeningId = Integer.toString(screeningList.get(recyclerView.getChildAdapterPosition(view)).getId());

                String selectedScreeningCinemaNumber = Integer.toString(screeningList.get(recyclerView.getChildAdapterPosition(view)).getCinemaNumber());

                String[] result = getRowNumber(selectedScreeningCinemaNumber);

                String rows = result[0];
                String columns = result[1];

                openSeatSelectionActivity(clientID, selectedMovieTheater, selectedMovieOriginalName, selectedScreeningId, selectedMovieImageURL, rows, columns);

            }

        });

        recyclerView.setAdapter(screeningAdapter);

    }

    private String[] getRowNumber(String selectedScreeningCinemaNumber) {

        String[] result = {"", ""};

        String rows = "";
        String columns = "";

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        // Getting rows and columns from cinema by number
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Rows, Columns FROM CINEMA WHERE Number =" + selectedScreeningCinemaNumber, null);

        while(cursor.moveToNext()) {

            rows = cursor.getString(0);
            columns = cursor.getString(1);

        }

        result[0] = rows;
        result[1] = columns;

        return result;

    }

    // Opens the activity where the user can select the seat and sends the previous information selected
    private void openSeatSelectionActivity(String clientID, String selectedMovieTheater, String selectedMovieOriginalName, String selectedScreeningId, String selectedMovieImageURL, String rows, String columns) {

        Intent intent = new Intent(this, SeatSelectionActivity.class);

        intent.putExtra("clientID", clientID);
        intent.putExtra("selectedMovieTheater", selectedMovieTheater);
        intent.putExtra("selectedMovieOriginalName", selectedMovieOriginalName);
        intent.putExtra("selectedScreeningId", selectedScreeningId);
        intent.putExtra("selectedMovieImageURL", selectedMovieImageURL);
        intent.putExtra("rows", rows);
        intent.putExtra("columns", columns);

        startActivity(intent);

    }

}
