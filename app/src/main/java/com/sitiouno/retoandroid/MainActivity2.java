package com.sitiouno.retoandroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity2 extends AppCompatActivity {

    private TextView titulo;
    private EditText et1, et2, et3;
    private Button button;
    private String id;
    private String fullname;
    private String email;
    private String code;
    private String method;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        titulo = findViewById(R.id.txt1);
        et1 = findViewById(R.id.name);
        et2 = findViewById(R.id.email);
        et3 = findViewById(R.id.code);
        button = findViewById(R.id.button);


        id = getIntent().getStringExtra("id");
        fullname = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        code = getIntent().getStringExtra("code");
        method = getIntent().getStringExtra("method");

        et1.setText(fullname);
        et2.setText(email);
        et3.setText(code);

        titulo.setText(method);
        button.setText(method);

        switch (method) {
            case "GET":
                button.setVisibility(View.GONE);

                TextInputLayout tl1 = (TextInputLayout)findViewById(R.id.caja1);
                tl1.setHelperTextEnabled(false);

                TextInputLayout tl2 = (TextInputLayout)findViewById(R.id.caja2);
                tl2.setHelperTextEnabled(false);

                TextInputLayout tl3 = (TextInputLayout)findViewById(R.id.caja3);
                tl3.setHelperTextEnabled(false);
                tl3.setCounterEnabled(false);

                et1.setEnabled(false);
                et2.setEnabled(false);
                et3.setEnabled(false);
                break;

            case "UPDATE":

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fullname = et1.getText().toString();
                        String email = et2.getText().toString();
                        int code = Integer.parseInt(et3.getText().toString());

                        updateUser(fullname, email, code, id);

                    }
                });
                System.out.println("Actualizado exitosamente");
                break;

            case "CREATE":
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (et1.getText().toString().isEmpty() || et2.getText().toString().isEmpty() || et3.getText().toString().isEmpty()) {
                            System.out.println("No se han ingresado todos los datos");
                        } else {
                            createUser(retrieveUser());
                        }
                    }
                });
                System.out.println("Metodo create");
                break;

            default:
                System.out.println(method);


        }
    }


    private void updateUser(String fullname, String email, int code, String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<Users> updateUser = retrofit.create(UsersInterface.class).updateUser(fullname, email, code, id);


        updateUser.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(MainActivity2.this, "", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(MainActivity2.this, "Error 400", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(MainActivity2.this, "Error 404", Toast.LENGTH_SHORT).show();
                        default:
                            Toast.makeText(MainActivity2.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Users user1 = response.body();

                    String _id = user1.getId();
                    String fullname = et1.getText().toString();
                    String email = et2.getText().toString();
                    String code = et3.getText().toString();

                    //OPEN DB SQLite
                    AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(MainActivity2.this);

                    adminSQLiteOpenHelper.updateUsers(_id, fullname, email, code);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });


    }

    private Users retrieveUser() {
        Users user = new Users();

        user.setFullname(et1.getText().toString());
        user.setEmail(et2.getText().toString());
        user.setCode(Integer.parseInt(et3.getText().toString()));

        return user;
    }

    private void createUser(Users user) {


        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<Users> call = retrofit.create(UsersInterface.class).saveUser(user);



        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {

                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(MainActivity2.this, "500", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(MainActivity2.this, "400", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(MainActivity2.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Users user1 = response.body();

                    String _id = user1.getId();
                    String fullname = et1.getText().toString();
                    String email = et2.getText().toString();
                    String code = et3.getText().toString();

                    //OPEN DB SQLite
                    AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(MainActivity2.this);

                    adminSQLiteOpenHelper.registerUser(_id, fullname, email, code);

                    System.out.println("Usuario creado exitosamente");
                    finish();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Error: " + t.getMessage());
            }
        });


    }


}