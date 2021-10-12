package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cinetec.adapters.ScreeningAdapter;
import com.example.cinetec.models.Cinema;
import com.example.cinetec.models.Screening;
import java.util.ArrayList;
import java.util.List;

public class ScreeningSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screening_selection);

        recyclerView = findViewById(R.id.recyclerViewScreening);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();

        String selectedMovieTheater = bundle.getString("selectedMovieTheater");
        String selectedMovieOriginalName = bundle.getString("selectedMovieOriginalName");
        String selectedMovieImageURL = bundle.getString("selectedMovieImageURL");

        movieList = findViewById(R.id.textViewScreeningList);

        movieList.setText(selectedMovieTheater + " Screening List");

        getScreeningsInformation(selectedMovieTheater, selectedMovieOriginalName, selectedMovieImageURL);

    }

    private void getScreeningsInformation(String selectedMovieTheater, String selectedMovieOriginalName, String selectedMovieImageURL) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

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

                String selection = Integer.toString(screeningList.get(recyclerView.getChildAdapterPosition(view)).getCinemaNumber());

                Toast.makeText(ScreeningSelectionActivity.this,"Selection = " + selection, Toast.LENGTH_SHORT).show();

                //openScreeningSelectionActivity(selectedMovieTheater);

            }
        });

        recyclerView.setAdapter(screeningAdapter);
























    }






































}