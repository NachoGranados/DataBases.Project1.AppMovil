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
import com.example.cinetec.adapters.SeatHorizontalAdapter;
import com.example.cinetec.adapters.SeatVerticalAdapter;
import com.example.cinetec.models.MovieTheater;
import com.example.cinetec.models.Seat;
import com.example.cinetec.models.SeatList;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVertical;

    //private List<SeatList> seatListList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        recyclerViewVertical = findViewById(R.id.recyclerViewSeatVertical);
        recyclerViewVertical.setHasFixedSize(true);
        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();

        String selectedScreeningId = bundle.getString("selectedScreeningId");
        int rows = Integer.parseInt(bundle.getString("rows"));
        int columns = Integer.parseInt(bundle.getString("columns"));

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

        SeatVerticalAdapter seatVerticalAdapter = new SeatVerticalAdapter(SeatSelectionActivity.this, seatListList);
        recyclerViewVertical.setAdapter(seatVerticalAdapter);





















        //SeatList seatListList = new SeatList();

        //seatListList.setSeatList(seatList);

        //seatListListGlobal.add(seatListList);

        //seatVerticalAdapter.notifyDataSetChanged();




        //recyclerViewVertical.setAdapter(screeningAdapter);



        //SeatHorizontalAdapter seatHorizontalAdapter = new SeatHorizontalAdapter(SeatSelectionActivity.this, seatList);

        /*
        seatHorizontalAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String selectedMovieTheater = movieTheaterList.get(recyclerView.getChildAdapterPosition(view)).getName();

                //openMovieSelectionActivity(selectedMovieTheater);

                //String selectedScreeningId = Integer.toString(seatList.get(recyclerView.getChildAdapterPosition(view)).getId());

                //Toast.makeText(SeatSelectionActivity.this,"Selection = " + selectedScreeningId, Toast.LENGTH_SHORT).show();

            }
        });

        */


        //recyclerView.setAdapter(movieTheaterAdapter);

    }

}