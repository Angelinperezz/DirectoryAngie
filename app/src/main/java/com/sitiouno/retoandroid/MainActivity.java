package com.sitiouno.retoandroid;

import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView txtBuscar;
    RecyclerView recyclerView;
    public static Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.floatingActionButton);
        txtBuscar = findViewById(R.id.txtBuscar);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.recycler);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

                getUsers();
            }
        });

        setRecyclerView();
        getUsers();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1 = new Intent(MainActivity.this, MainActivity2.class);
                int1.putExtra("method", "CREATE");
                startActivity(int1);
            }
        });
        txtBuscar.setOnQueryTextListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getUsers();
    }

    //Obtenemos los usuarios de la base de datos
    public static void getUsers() {

        //Crear conexion al API
        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        //Creando la llamada al endpoint del api correspondiente
        Call<List<Users>> call = retrofit.create(UsersInterface.class).getUsers();

        //Ejecutamos la llamada
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                //Si la respuesta NO es satisfactoria
                if (!response.isSuccessful()) {

                    //Obtener el codigo de respuesta de la peticion para poder controlar las validaciones
                    switch (response.code()) {
                        case 500:
                            System.out.println("500");
                            break;
                        case 404:
                            System.out.println("400");
                            break;
                        default:
                            System.out.println("Error: " + response.code());
                    }

                    //Si la respuesta es satisfactoria
                } else {
                    List<Users> users = response.body();
                    populateUsers(users);
                }

            }

            //En el caso de que la peticion falle
            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });

    }

    //Poblamos la data en las tarjetas mediante el adapter
    private static void populateUsers(List<Users> usersList) {
        List<Datos> data = new ArrayList<>();

        for (Users user : usersList) {
            data.add(new Datos(user.getFullname(), user.getEmail(), user.getCode(), user.getId()));
        }
        adapter.update(data);
    }

    //Configurar el recycler view
    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new Adapter(MainActivity.this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onQueryTextChange(String s) {

        adapter.filtrado(s);
        return false;
    }
}