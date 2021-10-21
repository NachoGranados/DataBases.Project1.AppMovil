package com.example.cinetec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class AdministratorSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdministratorSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

    }

    // Tables creation of the SQLite Data Base
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE CLIENT(ID INT PRIMARY KEY, First_name VARCHAR(20), Last_name VARCHAR(20), Sec_last_name VARCHAR(20), " +
                                                   "Age INT, Birth_date DATE, Phone_number VARCHAR(20), Password VARCHAR(20), Sync_status INT)");

        sqLiteDatabase.execSQL("CREATE TABLE MOVIE_THEATER(Name VARCHAR(20) PRIMARY KEY, Location VARCHAR(20), Cinema_amount INT)");

        sqLiteDatabase.execSQL("CREATE TABLE CINEMA(Number INT PRIMARY KEY, Rows INT, Columns INT, Capacity INT, Name_movie_theater VARCHAR(20))");

        sqLiteDatabase.execSQL("CREATE TABLE MOVIE(Original_name VARCHAR(20) PRIMARY KEY, Gendre VARCHAR(20), Name VARCHAR(20), Director VARCHAR(20)," +
                                                  "Image_url VARCHAR(350), Lenght INT)");

        sqLiteDatabase.execSQL("CREATE TABLE SCREENING(ID INT PRIMARY KEY, Cinema_number INT, Movie_original_name VARCHAR(20), Hour INT, Capacity INT)");

        sqLiteDatabase.execSQL("CREATE TABLE ACTOR(Original_movie_name VARCHAR(20), Actor_name VARCHAR(20), PRIMARY KEY (Original_movie_name, Actor_name))");

        sqLiteDatabase.execSQL("CREATE TABLE SEAT(Screening_id INT, Row_num INT, Column_num INT, State VARCHAR(20), Sync_status INT, PRIMARY KEY (Screening_id, Row_num, Column_num))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Deletes all the tables and the information on them and creates again the same tables deleted
    public void restartDataBase(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("DROP TABLE CLIENT");
        sqLiteDatabase.execSQL("DROP TABLE MOVIE_THEATER");
        sqLiteDatabase.execSQL("DROP TABLE CINEMA");
        sqLiteDatabase.execSQL("DROP TABLE MOVIE");
        sqLiteDatabase.execSQL("DROP TABLE SCREENING");
        sqLiteDatabase.execSQL("DROP TABLE ACTOR");
        sqLiteDatabase.execSQL("DROP TABLE SEAT");

        sqLiteDatabase.execSQL("CREATE TABLE CLIENT(ID INT PRIMARY KEY, First_name VARCHAR(20), Last_name VARCHAR(20), Sec_last_name VARCHAR(20), " +
                                                    "Age INT, Birth_date DATE, Phone_number VARCHAR(20), Password VARCHAR(20), Sync_status INT)");

        sqLiteDatabase.execSQL("CREATE TABLE MOVIE_THEATER(Name VARCHAR(20) PRIMARY KEY, Location VARCHAR(20), Cinema_amount INT)");

        sqLiteDatabase.execSQL("CREATE TABLE CINEMA(Number INT PRIMARY KEY, Rows INT, Columns INT, Capacity INT, Name_movie_theater VARCHAR(20))");

        sqLiteDatabase.execSQL("CREATE TABLE MOVIE(Original_name VARCHAR(20) PRIMARY KEY, Gendre VARCHAR(20), Name VARCHAR(20), Director VARCHAR(20)," +
                                             "Image_url VARCHAR(350), Lenght INT)");

        sqLiteDatabase.execSQL("CREATE TABLE SCREENING(ID INT PRIMARY KEY, Cinema_number INT, Movie_original_name VARCHAR(20), Hour INT, Capacity INT)");

        sqLiteDatabase.execSQL("CREATE TABLE ACTOR(Original_movie_name VARCHAR(20), Actor_name VARCHAR(20), PRIMARY KEY (Original_movie_name, Actor_name))");

        sqLiteDatabase.execSQL("CREATE TABLE SEAT(Screening_id INT, Row_num INT, Column_num INT, State VARCHAR(20), Sync_status INT, PRIMARY KEY (Screening_id, Row_num, Column_num))");

    }

}























