package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    // Variables to control XML items
    private Button registerButton;
    private EditText idText;
    private EditText passwordText;

    private ArrayList<Integer> screeningIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(this, new String[] {

                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        // Variables assignment to control XML items
        idText = findViewById(R.id.editTextLoginID);
        passwordText = findViewById(R.id.editTextLoginPassword);

        registerButton = (Button) findViewById(R.id.buttonLoginRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRegisterActivity();

            }

        });

        // Post the unsync information to the Rest API from SQLite Data Base
        if(checkInternetConnection()) {

            postUnsyncInformation();

            refreshDataBase();

        }

    }

    // Opens the activity where the user can register a new account
    private void openRegisterActivity() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    // Verify if the user information given is correct based on the SQLite Data base
    public void getClientFromSQLite(View view) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        String id = idText.getText().toString();
        String password = passwordText.getText().toString();

        if(!id.isEmpty() && !password.isEmpty()) {

            // Getting password from client by id
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

                    openMovieTheaterSelectionActivity(id);


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

    // Opens the activity where the user can select the movie theater
    private void openMovieTheaterSelectionActivity(String clientID) {

        Intent intent = new Intent(this, MovieTheaterSelectionActivity.class);

        intent.putExtra("clientID", clientID);

        startActivity(intent);

    }

    // Checks if the emulator or phone is connected to internet
    private boolean checkInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

    // Saves the information of clients and sets in the SQLite that has not been posted inf the Rest API
    private void postUnsyncInformation() {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        // Client posts
        // Getting client by sync status
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
        // Getting seat by sync status
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM SEAT WHERE Sync_status = 0", null);

        while(cursor.moveToNext()) {

            String screeningId = cursor.getString(0);
            String rowNum = cursor.getString(1);
            String columnNum = cursor.getString(2);

            Seat seat = new Seat();

            seat.setScreeningId(Integer.parseInt(screeningId));
            seat.setRowNum(Integer.parseInt(rowNum));
            seat.setColumnNum(Integer.parseInt(columnNum));
            seat.setState("sold");

            updateSeat(seat);

            // Deleting seat by screening id, row number and column number
            sqLiteDatabase.execSQL("DELETE FROM SEAT WHERE Screening_id=" + seat.getScreeningId() + " AND Row_num=" + seat.getRowNum() + " AND Column_num=" + seat.getColumnNum());

            ContentValues contentValues = new ContentValues();

            contentValues.put("Screening_id", seat.getScreeningId());
            contentValues.put("Row_num", seat.getRowNum());
            contentValues.put("Column_num", seat.getColumnNum());
            contentValues.put("State", "sold");
            contentValues.put("Sync_status", 1);

            sqLiteDatabase.insert("SEAT", null, contentValues);

        }

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a POST method of clients
    private void postClient(Client client) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Connects to the Rest API and applies a PUT method of seats
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

                        Toast.makeText(LoginActivity.this, "API Update Failed", Toast.LENGTH_SHORT).show();

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

    // Deletes all the SQLite tables, creates them again and saves the actual information from the Rest API
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

        getSeatsInformation();

    }

    // Connects to the Rest API and applies a GET method of clients
    private void getClientsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the clients information got in the Rest API into the SQLite Data Base
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

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of movie theaters
    private void getMovieTheatersInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the movie theaters information got in the Rest API into the SQLite Data Base
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

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of cinemas
    private void getCinemasInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the cinemas information got in the Rest API into the SQLite Data Base
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
            contentValues.put("Name_movie_theater", cinema.getNameMovieTheater());

            sqLiteDatabase.insert("CINEMA", null, contentValues);

        }

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of movies
    private void getMoviesInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the movies information got in the Rest API into the SQLite Data Base
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

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of screenings
    private void getScreeningsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the screenings information got in the Rest API into the SQLite Data Base
    private void addScreenings(List<Screening> screeningList) {

        AdministratorSQLiteOpenHelper administratorSQLiteOpenHelper = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administratorSQLiteOpenHelper.getWritableDatabase();

        screeningIdList.clear();

        for(int i = 0; i < screeningList.size(); i++) {

            Screening screening = screeningList.get(i);

            screeningIdList.add(screening.getId());

            ContentValues contentValues = new ContentValues();

            contentValues.put("ID", screening.getId());
            contentValues.put("Cinema_number", screening.getCinemaNumber());
            contentValues.put("Movie_original_name", screening.getMovieOriginalName());
            contentValues.put("Hour", screening.getHour());
            contentValues.put("Capacity", screening.getCapacity());

            sqLiteDatabase.insert("SCREENING", null, contentValues);

        }

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of actors
    private void getActorsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
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

    // Inserts the actors information got in the Rest API into the SQLite Data Base
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

        sqLiteDatabase.close();

    }

    // Connects to the Rest API and applies a GET method of seats
    private void getSeatsInformation() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:5000/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        SeatRestAPI seatRestAPI = retrofit.create(SeatRestAPI.class);

        for (int i = 0; i < screeningIdList.size(); i++) {

            Call<List<Seat>> call = seatRestAPI.getSeats(screeningIdList.get(i));
            call.enqueue(new Callback<List<Seat>>() {

                @Override
                public void onResponse(Call<List<Seat>> call, Response<List<Seat>> response) {

                    try {

                        if (response.isSuccessful()) {

                            List<Seat> seatList = response.body();

                            addSeats(seatList);

                        } else {

                            Toast.makeText(LoginActivity.this, "Unsuccessful Seats GET", Toast.LENGTH_SHORT).show();

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



    }

    // Inserts the seats information got in the Rest API into the SQLite Data Base
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
            contentValues.put("Sync_status", 1);

            sqLiteDatabase.insert("SEAT", null, contentValues);

        }

        sqLiteDatabase.close();

    }

}