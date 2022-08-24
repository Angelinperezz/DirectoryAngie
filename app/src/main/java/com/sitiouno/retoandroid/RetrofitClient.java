package com.sitiouno.retoandroid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static String BASE_URL = "https://reto-android.herokuapp.com/api/v1/";
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofitClient() {

        if (retrofit == null) {
            //Creamos un interceptor y le indicamos el log level a usar
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);

            //Asociamos el interceptor a las peticiones
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(60, TimeUnit.SECONDS);
            client.readTimeout(60, TimeUnit.SECONDS);
            client.writeTimeout(60, TimeUnit.SECONDS);
            client.addInterceptor(interceptor);

            gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();
            /*Hasta aqui hemos declarado nuestro metodo y tiene sus caracteristicas definidas, pero aun no han sido
            implementadas*/

            /*
            Con estas ultimas lineas del retrofit, creamos e implementamos cada una de estas caracteristicas por eso
            se nombran como metodo cada una, y en esta parte del codigo es que la libreria GSON se encarga de hacer la
            transformacion
            para que todos los objetos sean de tipo String
            GSON es una librería de código abierto que permite serializar objetos Java para convertirlos en un String.
            */
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build();

        }
        return retrofit;

    }
}
