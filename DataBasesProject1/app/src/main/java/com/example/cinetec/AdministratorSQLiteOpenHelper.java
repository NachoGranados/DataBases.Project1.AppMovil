package com.example.cinetec;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdministratorSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdministratorSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table CLIENT(ID int primary key, First_name varchar(20), Last_name varchar(20), Sec_last_name varchar(20), " +
                               "Age int, Birth_date date, Phone_number varchar(20), Password varchar(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
