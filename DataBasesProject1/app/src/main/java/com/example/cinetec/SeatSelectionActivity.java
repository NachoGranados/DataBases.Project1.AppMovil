package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.adapters.SeatHorizontalAdapter;
import com.example.cinetec.adapters.SeatVerticalAdapter;
import com.example.cinetec.models.MovieTheater;
import com.example.cinetec.models.Seat;
import com.example.cinetec.models.SeatList;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVertical;

    private static List<Seat> selectedSeatList;

    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        recyclerViewVertical = findViewById(R.id.recyclerViewSeatVertical);
        recyclerViewVertical.setHasFixedSize(true);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(this));

        selectedSeatList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        String selectedMovieTheater = bundle.getString("selectedMovieTheater");
        String selectedMovieOriginalName = bundle.getString("selectedMovieOriginalName");
        String selectedScreeningId = bundle.getString("selectedScreeningId");
        String selectedMovieImageURL = bundle.getString("selectedMovieImageURL");
        int rows = Integer.parseInt(bundle.getString("rows"));
        int columns = Integer.parseInt(bundle.getString("columns"));

        continueButton = findViewById(R.id.buttonSeatList);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openConfirmationActivity(selectedMovieTheater, selectedMovieOriginalName, selectedScreeningId, selectedMovieImageURL);

            }

        });

        getSeatsInformation(selectedScreeningId, rows, columns);

    }

    private void getSeatsInformation(String selectedScreeningId, int rows, int columns) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM SEAT WHERE Screening_id =" + selectedScreeningId, null);

        List<SeatList> seatListList = new ArrayList<>();

        List<Seat> seatList = new ArrayList<>();

        int counter = 0;

        while(cursor.moveToNext()) {

            if(counter < columns) {

                String screeningId = cursor.getString(0);
                String rowNum = cursor.getString(1);
                String columnNum = cursor.getString(2);
                String state = cursor.getString(3);

                Seat seat = new Seat();

                seat.setScreeningId(Integer.parseInt(screeningId));
                seat.setRowNum(Integer.parseInt(rowNum));
                seat.setColumnNum(Integer.parseInt(columnNum));
                seat.setState(state);

                seatList.add(seat);

                counter ++;

                if(Integer.parseInt(rowNum) == rows && Integer.parseInt(columnNum) == columns) {

                    SeatList seatListItem = new SeatList();

                    seatListItem.setSeatList(seatList);

                    seatListList.add(seatListItem);

                }

            } else {

                SeatList seatListItem = new SeatList();

                seatListItem.setSeatList(seatList);

                seatListList.add(seatListItem);

                seatList = new ArrayList<>();

                String screeningId = cursor.getString(0);
                String rowNum = cursor.getString(1);
                String columnNum = cursor.getString(2);
                String state = cursor.getString(3);

                Seat seat = new Seat();

                seat.setScreeningId(Integer.parseInt(screeningId));
                seat.setRowNum(Integer.parseInt(rowNum));
                seat.setColumnNum(Integer.parseInt(columnNum));
                seat.setState(state);

                seatList.add(seat);

                counter = 1;

            }

        }

        SeatVerticalAdapter seatVerticalAdapter = new SeatVerticalAdapter(SeatSelectionActivity.this, seatListList, this);
        recyclerViewVertical.setAdapter(seatVerticalAdapter);

    }

    public void addSelectedSeat(Seat seat) {

        if(!selectedSeatList.contains(seat)) {

            selectedSeatList.add(seat);

        }

    }

    public void deleteSelectedSeat(Seat seat) {

        if(selectedSeatList.contains(seat)) {

            selectedSeatList.remove(seat);

        }

    }

    public static List<Seat> getSelectedSeatList() {

        return selectedSeatList;

    }

    public void openConfirmationActivity(String selectedMovieTheater, String selectedMovieOriginalName, String selectedScreeningId, String selectedMovieImageURL) {

        Intent intent = new Intent(this, ConfirmationActivity.class);

        intent.putExtra("selectedMovieTheater", selectedMovieTheater);
        intent.putExtra("selectedMovieOriginalName", selectedMovieOriginalName);
        intent.putExtra("selectedScreeningId", selectedScreeningId);
        intent.putExtra("selectedMovieImageURL", selectedMovieImageURL);

        startActivity(intent);

    }

}


























