package com.sitiouno.retoandroid;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsersInterface {

    @GET ("users")
    Call<List<Users>> getUsers();

    @GET("users/{id}")
    Call<Users> getUsersbyId(@Path("id") String id);

    @POST("users/create")
    Call<Users> saveUser(@Body Users user);

    @FormUrlEncoded
    @PUT("users/update/{id}")
    Call<Users> updateUser(@Field("fullname") String fullname, @Field("email") String email, @Field("code") int code, @Path ("id") String id);

    @DELETE("users/delete/{id}")
    Call<Users> deleteUser(@Path ("id") String id);

}
