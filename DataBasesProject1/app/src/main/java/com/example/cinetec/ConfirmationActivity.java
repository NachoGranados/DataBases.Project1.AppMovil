package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.cinetec.adapters.ConfirmationSeatAdapter;
import com.example.cinetec.adapters.MovieAdapter;
import com.example.cinetec.adapters.MovieTheaterAdapter;
import com.example.cinetec.adapters.ScreeningAdapter;
import com.example.cinetec.interfaces.SeatRestAPI;
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.MovieTheater;
import com.example.cinetec.models.Screening;
import com.example.cinetec.models.Seat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMovieTheater;
    private RecyclerView recyclerViewMovie;
    private RecyclerView recyclerViewScreening;
    private RecyclerView recyclerViewSeat;

    private Button purchaseButton;

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

        String clientID = bundle.getString("clientID");
        String selectedMovieTheater = bundle.getString("selectedMovieTheater");
        String selectedMovieOriginalName = bundle.getString("selectedMovieOriginalName");
        String selectedScreeningId = bundle.getString("selectedScreeningId");
        String selectedMovieImageURL = bundle.getString("selectedMovieImageURL");

        List<Seat> seatList = SeatSelectionActivity.getSelectedSeatList();

        purchaseButton = findViewById(R.id.buttonConfirmationPurchase);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSeatInformation(seatList);

                generateInvoice(clientID, selectedMovieTheater, selectedMovieOriginalName, selectedScreeningId, seatList.size());

                openMovieTheaterSelectionActivity(clientID);

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

    private void openMovieTheaterSelectionActivity(String clientID) {

        Intent intent = new Intent(this, MovieTheaterSelectionActivity.class);

        intent.putExtra("clientID", clientID);

        startActivity(intent);

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

    private void updateSeatInformation(List<Seat> seatList) {

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

                seat.setState("sold");

                updateSeat(seat);

            } else {

                contentValues.put("Sync_status", 0);

            }

            sqLiteDatabase.insert("SEAT", null, contentValues);

        }

        Toast.makeText(ConfirmationActivity.this, "Successful purchase", Toast.LENGTH_SHORT).show();

        sqLiteDatabase.close();

    }

    private void updateSeat(Seat seat) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        SeatRestAPI seatRestAPI = retrofit.create(SeatRestAPI.class);

        Call<Seat> call = seatRestAPI.putSeat(seat.getScreeningId(), seat.getRowNum(), seat.getColumnNum(), seat);
        call.enqueue(new Callback<Seat>() {
            @Override
            public void onResponse(Call<Seat> call, retrofit2.Response<Seat> response) {

                try {

                    if (!response.isSuccessful()) {

                        Toast.makeText(ConfirmationActivity.this, "API Update Failed", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    //Toast.makeText(RegisterActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<Seat> call, Throwable t) {

                //Toast.makeText(RegisterActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void generateInvoice(String clientID, String selectedMovieTheater, String selectedMovieOriginalName, String selectedScreeningId, int seatListSize) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint titilePaint = new Paint();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Bitmap bitmapLogo = Bitmap.createScaledBitmap(bitmap, 580, 300, false);

        canvas.drawBitmap(bitmapLogo, -100, 120, titilePaint);

        titilePaint.setTextAlign(Paint.Align.RIGHT);
        titilePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titilePaint.setTextSize(70);
        canvas.drawText("CineTEC", 1150, 150, titilePaint);

        titilePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        titilePaint.setTextSize(35);
        canvas.drawText("Barrio Escalante, Avenida 7, San José, Costa Rica", 1150, 250, titilePaint);
        canvas.drawText("(+506) 2555 5555", 1150, 300, titilePaint);
        canvas.drawText("costumer_service@cinetec.com", 1150, 350, titilePaint);
        canvas.drawText("www.cinetec.cr", 1150, 450, titilePaint);

        canvas.drawLine(50, 500, 1150, 500, titilePaint);

        Paint paint = new Paint();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM CLIENT WHERE ID=" + clientID, null);

        String firstName = "";
        String lastName = "";
        String secLastName = "";
        String phoneNumber = "";

        while(cursor.moveToNext()) {

            firstName = cursor.getString(1);
            lastName = cursor.getString(2);
            secLastName = cursor.getString(3);
            phoneNumber = cursor.getString(6);

        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(35);
        canvas.drawText("Invoice issued for:", 50, 550, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(50);
        canvas.drawText(firstName + " " + lastName + " " + secLastName, 50, 600, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(35);
        canvas.drawText("(+506) " + phoneNumber, 50, 700, paint);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Payment Date: " + dateFormat.format(date) + " " + timeFormat.format(date), 1150, 700, paint);
        canvas.drawText("Invoice Date: " + dateFormat.format(date) + " " + timeFormat.format(date), 1150, 750, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Movie", 50, 850, paint);
        canvas.drawText("Screening", 300, 850, paint);
        canvas.drawText("Theater", 500, 850, paint);
        canvas.drawText("Price", 700, 850, paint);
        canvas.drawText("Quantity", 850, 850, paint);
        canvas.drawText("Total", 1050, 850, paint);

        canvas.drawLine(50, 870, 1150, 870, paint);

        cursor = sqLiteDatabase.rawQuery("SELECT * FROM SCREENING WHERE ID=" + selectedScreeningId, null);

        String hour = "";

        while(cursor.moveToNext()) {

            hour = cursor.getString(3);

        }

        String subTotal = Integer.toString(3097 * seatListSize);

        canvas.drawText(selectedMovieOriginalName, 50, 920, paint);
        canvas.drawText(hour + ":00", 330, 920, paint);
        canvas.drawText(selectedMovieTheater, 500, 920, paint);
        canvas.drawText("₡ 3097", 680, 920, paint);
        canvas.drawText(Integer.toString(seatListSize), 910, 920, paint);
        canvas.drawText("₡ " + subTotal, 1040, 920, paint);

        canvas.drawLine(690, 1000, 1150, 1000, paint);

        String total = Integer.toString(3500 * seatListSize);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Subtotal:", 830, 1050, paint);
        canvas.drawText("₡ " + subTotal, 1150, 1050, paint);

        canvas.drawText("IVA:", 830, 1100, paint);
        canvas.drawText("13 %", 1150, 1100, paint);

        canvas.drawText("Total:", 830, 1150, paint);
        canvas.drawText("₡ " + total, 1150, 1150, paint);

        pdfDocument.finishPage(page);

        String pathFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File file = new File(pathFile, "/CineTEC_Invoice.pdf");

        try {

            pdfDocument.writeTo(new FileOutputStream(file));

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        pdfDocument.close();

    }

}
