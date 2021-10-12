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
import android.widget.EditText;
import android.widget.Toast;

import com.example.cinetec.interfaces.ClientRestAPI;
import com.example.cinetec.models.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText idText;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText secLastNameText;
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
        secLastNameText = findViewById(R.id.editTextRegisterSecondLastName);
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
        String secLastName = secLastNameText.getText().toString();
        String age = ageText.getText().toString();
        String birthDate = birthDateText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();
        String password = passwordText.getText().toString();

        if(!id.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !secLastName.isEmpty() && !age.isEmpty() && !birthDate.isEmpty() &&
           !phoneNumber.isEmpty() && !password.isEmpty()) {

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM CLIENT WHERE ID =" + id, null);

            if(cursor.moveToFirst()) {

                Toast.makeText(this, "Account already created. Try Again", Toast.LENGTH_SHORT).show();

                idText.setText("");
                firstNameText.setText("");
                lastNameText.setText("");
                secLastNameText.setText("");
                ageText.setText("");
                birthDateText.setText("");
                phoneNumberText.setText("");
                passwordText.setText("");

                sqLiteDatabase.close();

            } else {

                ContentValues contentValues = new ContentValues();

                contentValues.put("ID", Integer.parseInt(id));
                contentValues.put("First_name", firstName);
                contentValues.put("Last_name", lastName);
                contentValues.put("Sec_last_name", secLastName);
                contentValues.put("Age", Integer.parseInt(age));
                contentValues.put("Birth_date", birthDate);
                contentValues.put("Phone_number", phoneNumber);
                contentValues.put("Password", password);

                if(checkInternetConnection()) {

                    contentValues.put("Sync_status", "1");

                    sqLiteDatabase.insert("CLIENT", null, contentValues);

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

                } else {

                    contentValues.put("Sync_status", "0");

                    sqLiteDatabase.insert("CLIENT", null, contentValues);

                }

                Toast.makeText(this, "Successful register", Toast.LENGTH_SHORT).show();

                sqLiteDatabase.close();

                idText.setText("");
                firstNameText.setText("");
                lastNameText.setText("");
                secLastNameText.setText("");
                ageText.setText("");
                birthDateText.setText("");
                phoneNumberText.setText("");
                passwordText.setText("");

                openLoginActivity();

            }

        } else {

            Toast.makeText(this, "Complete all the information", Toast.LENGTH_SHORT).show();

        }

    }

    private void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

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

                    if (response.isSuccessful()) {


                        //Toast.makeText(RegisterActivity.this, "API Register Failed", Toast.LENGTH_SHORT).show();

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

}
