package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.cinetec.interfaces.ActorRestAPI;
import com.example.cinetec.interfaces.CinemaRestAPI;
import com.example.cinetec.interfaces.ClientRestAPI;
import com.example.cinetec.interfaces.MovieRestAPI;
import com.example.cinetec.interfaces.MovieTheaterRestAPI;
import com.example.cinetec.interfaces.ScreeningRestAPI;
import com.example.cinetec.interfaces.SeatRestAPI;
import com.example.cinetec.models.Actor;
import com.example.cinetec.models.Cinema;
import com.example.cinetec.models.Client;
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.MovieTheater;
import com.example.cinetec.models.Screening;
import com.example.cinetec.models.Seat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Button registerButton;

    private EditText idText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = findViewById(R.id.editTextLoginID);
        passwordText = findViewById(R.id.editTextLoginPassword);

        registerButton = (Button) findViewById(R.id.buttonLoginRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRegisterActivity();

            }

        });

        if(checkInternetConnection()) {

            postUnsyncInformation();

            refreshDataBase();

        }

    }

    private void openRegisterActivity() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    public void getClientFromSQLite(View view) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        String id = idText.getText().toString();
        String password = passwordText.getText().toString();

        if(!id.isEmpty() && !password.isEmpty()) {

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT Password FROM CLIENT WHERE ID =" + id, null);

            if(cursor.moveToFirst()) {

                if (cursor.getString(0).equals(password)) {

                    Toast.makeText(this, "Welcome to CineTEC", Toast.LENGTH_SHORT).show();

                    sqLiteDatabase.close();

                    idText.setText("");
                    passwordText.setText("");

                    if(checkInternetConnection()) {

                        postUnsyncInformation();

                        refreshDataBase();

                    }

                    openMovieTheaterSelectionActivity();


                } else {

                    Toast.makeText(this, "Wrong password. Try again", Toast.LENGTH_SHORT).show();

                    passwordText.setText("");

                }

            } else {

                Toast.makeText(this, "Account not created", Toast.LENGTH_SHORT).show();

                sqLiteDatabase.close();

                idText.setText("");
                passwordText.setText("");

            }

        } else {

            Toast.makeText(this, "Complete all the information", Toast.LENGTH_SHORT).show();

        }

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

    private void postUnsyncInformation() {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        // Clients posts
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM CLIENT WHERE Sync_status = 0", null);

        while(cursor.moveToNext()) {

            String id = cursor.getString(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String secLastName = cursor.getString(3);
            String age = cursor.getString(4);
            String birthDate = cursor.getString(5);
            String phoneNumber = cursor.getString(6);
            String password = cursor.getString(7);

            Client client = new Client();

            client.setId(Integer.parseInt(id));
            client.setFirstName(firstName);
            client.setLastName(lastName);
            client.setSecLastName(secLastName);
            client.setAge(Integer.parseInt(age));
            client.setBirthDate(birthDate);
            client.setPhoneNumber(phoneNumber);
            client.setPassword(password);

            postClient(client);

            ContentValues contentValues = new ContentValues();
            contentValues.put("Sync_status", "1");

            sqLiteDatabase.update("CLIENT", contentValues, "ID=?", new String[]{id});

        }

        // Seats posts

    }

    private void postClient(Client client) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ClientRestAPI clientRestAPI = retrofit.create(ClientRestAPI.class);

        Call<Client> call = clientRestAPI.postClient(client);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, retrofit2.Response<Client> response) {

                try {

                    if (!response.isSuccessful()) {


                        Toast.makeText(LoginActivity.this, "API Register Failed", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    //Toast.makeText(RegisterActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {

                //Toast.makeText(RegisterActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void refreshDataBase() {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        administratorSQLiteOpenHelper.restartDataBase(sqLiteDatabase);

        getClientsInformation();

        getMovieTheatersInformation();

        getCinemasInformation();

        getMoviesInformation();

        getScreeningsInformation();

        getActorsInformation();

        //getSeatsInformation();

    }

    private void getClientsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ClientRestAPI clientRestAPI = retrofit.create(ClientRestAPI.class);

        Call<List<Client>> call = clientRestAPI.getClients();
        call.enqueue(new Callback<List<Client>>() {

            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Client> clientList = response.body();

                        addClients(clientList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Clients GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addClients(List<Client> clientList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < clientList.size(); i++) {

            Client client = clientList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("ID", client.getId());
            contentValues.put("First_name", client.getFirstName());
            contentValues.put("Last_name", client.getLastName());
            contentValues.put("Sec_last_name", client.getSecLastName());
            contentValues.put("Age", client.getAge());
            contentValues.put("Birth_date", client.getBirthDate());
            contentValues.put("Phone_number", client.getPhoneNumber());
            contentValues.put("Password", client.getPassword());
            contentValues.put("Sync_status", "1");

            sqLiteDatabase.insert("CLIENT", null, contentValues);

        }

    }

    private void getMovieTheatersInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        MovieTheaterRestAPI movieTheaterRestAPI = retrofit.create(MovieTheaterRestAPI.class);

        Call<List<MovieTheater>> call = movieTheaterRestAPI.getMovieTheaters();
        call.enqueue(new Callback<List<MovieTheater>>() {

            @Override
            public void onResponse(Call<List<MovieTheater>> call, Response<List<MovieTheater>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<MovieTheater> movieTheaterList = response.body();

                        addMovieTheaters(movieTheaterList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Movie Theaters GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<MovieTheater>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addMovieTheaters(List<MovieTheater> movieTheaterList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < movieTheaterList.size(); i++) {

            MovieTheater movieTheater = movieTheaterList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Name", movieTheater.getName());
            contentValues.put("Location", movieTheater.getLocation());
            contentValues.put("Cinema_amount", movieTheater.getCinemaAmount());

            sqLiteDatabase.insert("MOVIE_THEATER", null, contentValues);

        }

    }

    private void getCinemasInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        CinemaRestAPI cinemaRestAPI = retrofit.create(CinemaRestAPI.class);

        Call<List<Cinema>> call = cinemaRestAPI.getCinemas();
        call.enqueue(new Callback<List<Cinema>>() {

            @Override
            public void onResponse(Call<List<Cinema>> call, Response<List<Cinema>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Cinema> cinemaList = response.body();

                        addCinemas(cinemaList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Cinemas GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Cinema>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addCinemas(List<Cinema> cinemaList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < cinemaList.size(); i++) {

            Cinema cinema = cinemaList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Number", cinema.getNumber());
            contentValues.put("Rows", cinema.getRows());
            contentValues.put("Columns", cinema.getColumns());
            contentValues.put("Capacity", cinema.getCapacity());
            contentValues.put("Name_movie_theater", cinema.getNumber());

            sqLiteDatabase.insert("CINEMA", null, contentValues);

        }

    }

    private void getMoviesInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        MovieRestAPI movieRestAPI = retrofit.create(MovieRestAPI.class);

        Call<List<Movie>> call = movieRestAPI.getMovies();
        call.enqueue(new Callback<List<Movie>>() {

            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Movie> movieList = response.body();

                        addMovies(movieList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Movies GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addMovies(List<Movie> movieList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < movieList.size(); i++) {

            Movie movie = movieList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Original_name", movie.getOriginalName());
            contentValues.put("Gendre", movie.getGendre());
            contentValues.put("Name", movie.getName());
            contentValues.put("Director", movie.getDirector());
            contentValues.put("Image_url", movie.getImageUrl());
            contentValues.put("Lenght", movie.getLenght());

            sqLiteDatabase.insert("MOVIE", null, contentValues);

        }


    }

    private void getScreeningsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ScreeningRestAPI screeningRestAPI = retrofit.create(ScreeningRestAPI.class);

        Call<List<Screening>> call = screeningRestAPI.getScreenings();
        call.enqueue(new Callback<List<Screening>>() {

            @Override
            public void onResponse(Call<List<Screening>> call, Response<List<Screening>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Screening> screeningList = response.body();

                        addScreenings(screeningList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Screenings GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Screening>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addScreenings(List<Screening> screeningList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < screeningList.size(); i++) {

            Screening screening = screeningList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("ID", screening.getId());
            contentValues.put("Cinema_number", screening.getCinemaNumber());
            contentValues.put("Movie_original_name", screening.getMovieOriginalName());
            contentValues.put("Hour", screening.getHour());
            contentValues.put("Capacity", screening.getCapacity());

            sqLiteDatabase.insert("SCREENING", null, contentValues);

        }

    }

    private void getActorsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ActorRestAPI actorRestAPI = retrofit.create(ActorRestAPI.class);

        Call<List<Actor>> call = actorRestAPI.getActors();
        call.enqueue(new Callback<List<Actor>>() {

            @Override
            public void onResponse(Call<List<Actor>> call, Response<List<Actor>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Actor> actorList = response.body();

                        addActors(actorList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Actors GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Actor>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addActors(List<Actor> actorList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < actorList.size(); i++) {

            Actor actor = actorList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Original_movie_name", actor.getOriginalMovieName());
            contentValues.put("Actor_name", actor.getActorName());

            sqLiteDatabase.insert("ACTOR", null, contentValues);

        }

    }

    /*
    private void getSeatsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        SeatRestAPI seatRestAPI = retrofit.create(SeatRestAPI.class);

        Call<List<Seat>> call = seatRestAPI.getScreening();
        call.enqueue(new Callback<List<Seat>>() {

            @Override
            public void onResponse(Call<List<Seat>> call, Response<List<Seat>> response) {

                try {

                    if (response.isSuccessful()) {

                        List<Seat> seatList = response.body();

                        addSeats(seatList);

                    } else {

                        Toast.makeText(LoginActivity.this, "Unsuccessful Actors GET", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception exception) {

                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Seat>> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void addSeats(List<Seat> seatList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        for(int i = 0; i < seatList.size(); i++) {

            Seat seat = seatList.get(i);

            ContentValues contentValues = new ContentValues();

            contentValues.put("Screening_id", seat.getScreeningId());
            contentValues.put("Row_num", seat.getRowNum());
            contentValues.put("Column_num", seat.getColumnNum());
            contentValues.put("State", seat.getState());

            sqLiteDatabase.insert("SEAT", null, contentValues);

        }

    }
    */

}