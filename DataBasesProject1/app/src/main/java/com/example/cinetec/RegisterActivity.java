package com.example.cinetec;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText idText;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText secondLastNameText;
    private EditText ageText;
    private EditText birthDateText;
    private EditText phoneNumberText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = findViewById(R.id.editTextRegisterID);
        firstNameText = findViewById(R.id.editTextRegisterFirstName);
        lastNameText = findViewById(R.id.editTextRegisterLastName);
        secondLastNameText = findViewById(R.id.editTextRegisterSecondLastName);
        ageText = findViewById(R.id.editTextRegisterAge);
        birthDateText = findViewById(R.id.editTextRegisterBirthDate);
        phoneNumberText = findViewById(R.id.editTextRegisterPhoneNumber);
        passwordText = findViewById(R.id.editTextRegisterPassword);

    }

    public void postClient(View view) {

        AdministratorSQLiteOpenHelper administrator = new AdministratorSQLiteOpenHelper(this, "CineTEC", null, 1);
        SQLiteDatabase sqLiteDatabase = administrator.getWritableDatabase();

        String id = idText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String secondLastName = secondLastNameText.getText().toString();
        String age = ageText.getText().toString();
        String birthDate = birthDateText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();
        String password = passwordText.getText().toString();

        if(!id.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !secondLastName.isEmpty() && !age.isEmpty() && !birthDate.isEmpty() &&
           !phoneNumber.isEmpty() && !password.isEmpty()) {

            ContentValues register = new ContentValues();

            register.put("ID", id);
            register.put("First_name", firstName);
            register.put("Last_name", lastName);
            register.put("Sec_last_name", secondLastName);
            register.put("Age", age);
            register.put("Birth_date", birthDate);
            register.put("Phone_number", phoneNumber);
            register.put("Password", password);

            sqLiteDatabase.insert("CLIENT", null, register);

            sqLiteDatabase.close();

            idText.setText("");
            firstNameText.setText("");
            lastNameText.setText("");
            secondLastNameText.setText("");
            ageText.setText("");
            birthDateText.setText("");
            phoneNumberText.setText("");
            passwordText.setText("");

            Toast.makeText(this, "Successful register", Toast.LENGTH_SHORT).show();

            openLoginActivity();

        } else {

            Toast.makeText(this, "Complete all the information", Toast.LENGTH_SHORT).show();

        }

    }

    public void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

}
