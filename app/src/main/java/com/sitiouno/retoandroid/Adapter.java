package com.sitiouno.retoandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;

import static com.sitiouno.retoandroid.MainActivity.getUsers;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolderAdapter> {

    //Variables globales de almacenamiento
    private List<Datos> data;
    private Context context;
    private List<Datos> dataBuscador;

    //Constructor
    public Adapter(Context context, ArrayList<Datos> data) {
        this.context = context;
        this.data = data;
        dataBuscador = new ArrayList<>();
        dataBuscador.addAll(data);
    }

    //Metodo para actualizar la data
    public void update(List<Datos> list) {
        data = list;
        notifyDataSetChanged();
    }

    //Retornamos una nueva vista del elemento / se crea la vista
    @NonNull
    @Override
    public Adapter.MyViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderAdapter(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false)
        );
    }

    /* Asignar los datos y asignar funciones a los metodos en el caso de que sea necesario
     * Asigna valores para cada elemento de la lista, obtiene un elemento del dataset segun su posicion
     * reemplaza el contenido usando tales datos */

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderAdapter holder, int position) {
        if (data != null && data.size() > 0) {
            Datos datas = data.get(position);

            String id = data.get(position).getId();

            holder.name.setText("Nombre: " + datas.getName());
            holder.email.setText("Email: " + datas.getEmail());
            holder.code.setText("Code: " + String.valueOf(datas.getCode()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getUser(id, "GET");
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getUser(id, "UPDATE");
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog(id).show();

                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filtrado(String txtBuscar) {
        if (txtBuscar.length() == 0) {
            getUsers();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Datos> colection = (List<Datos>) data.stream()
                        .filter(i -> i.getName().toLowerCase().contains(txtBuscar.toLowerCase())).collect(Collectors
                                .toList());
                data.clear();
                data.addAll(colection);
            } else {
                for (Datos d : dataBuscador) {
                    if (d.getName().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        data.add(d);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    //retorna la cantidad total de elementos que tengamos
    @Override
    public int getItemCount() {
        return data.size();
    }

    private AlertDialog alertDialog(String id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(context.getDrawable(R.drawable.ic_baseline_restore_from_trash_24));
        builder.setTitle("Eliminar usuario");
        builder.setMessage("Estas seguro que quieres eliminar un usuario?");

        // Add the buttons
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // User clicked OK button
                deleteUser(id);
                getUsers();
            }
        });

        builder.setNegativeButton(R.string.Decline, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        return dialog;
    }

    private void deleteUser(String id) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();

        Call<Users> deleteUser = retrofit.create(UsersInterface.class).deleteUser(id);

        deleteUser.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(context, "400", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.out.println("Usuario eliminado");
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });

    }


    private void getUser(String id, String method) {

        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        Call<Users> getUser = retrofit.create(UsersInterface.class).getUsersbyId(id);

        getUser.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (!response.isSuccessful()) {
                    switch (response.code()) {
                        case 500:
                            Toast.makeText(context, "Error 500", Toast.LENGTH_SHORT).show();
                            break;
                        case 400:
                            Toast.makeText(context, "Error 400", Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(context, "Error 404", Toast.LENGTH_SHORT).show();
                        default:
                            Toast.makeText(context, "Error: " + response.code(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Users user = response.body();

                    String id = user.getId();
                    String name = user.getFullname();
                    String email = user.getEmail();
                    String code = String.valueOf(user.getCode());

                    Intent intent = new Intent(context, MainActivity2.class);


                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("code", code);
                    intent.putExtra("method", method);

                    context.startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });

    }

    public class MyViewHolderAdapter extends RecyclerView.ViewHolder {

        private TextView name, email, code;
        private ImageView edit, delete;

        public MyViewHolderAdapter(final View view) {
            super(view);
            name = view.findViewById(R.id.card_fullname);
            email = view.findViewById(R.id.card_email);
            code = view.findViewById(R.id.card_code);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }

    }

}
