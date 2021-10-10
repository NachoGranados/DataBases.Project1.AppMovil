package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    }

    private void openRegisterActivity() {

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    public void getClient(View view) {

        AdministratorSQLiteOpenHelper administrator = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administrator.getWritableDatabase();

        String id = idText.getText().toString();
        String password = passwordText.getText().toString();

        if(!id.isEmpty() && !password.isEmpty()) {

            Cursor row = sqLiteDatabase.rawQuery("select Password from CLIENT where ID =" + id, null);

            if(row.moveToFirst()) {

                //String getId = row.getString(0);
                String getFirstName = row.getString(0);
                //String getLastName = row.getString(1);
                //String getSecondLastName = row.getString(2);
                //String getAge = row.getString(3);
                //String getBirthDate = row.getString(4);1
                //String getPhoneNumber = row.getString(5);
                //String getPassword = row.getString(6);

                String info = "FN = " + getFirstName; //+
                              //LN = " + getLastName +
                              //"SLN = " + getSecondLastName +
                              //"A = " + getAge +
                              //"BD = " + getBirthDate +
                              //"PN = " + getPhoneNumber +
                              //"P = " + getPassword;

                Toast.makeText(this, info, Toast.LENGTH_SHORT).show();

                sqLiteDatabase.close();

            } else {

                Toast.makeText(this, "Account not created", Toast.LENGTH_SHORT).show();

                sqLiteDatabase.close();

            }

        } else {

            Toast.makeText(this, "Complete all the information", Toast.LENGTH_SHORT).show();

        }

    }

}