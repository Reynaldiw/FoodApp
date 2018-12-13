package com.reynaldiwijaya.foodapp.network;

import com.reynaldiwijaya.foodapp.model.ResponseDataMakanan;
import com.reynaldiwijaya.foodapp.model.ResponseKategori;
import com.reynaldiwijaya.foodapp.model.ResponseLogin;
import com.reynaldiwijaya.foodapp.model.ResponseRegister;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    /**
     * class interface ini mendefinisikan cara function / method
     */

    @FormUrlEncoded
    @POST("registeruser.php/")
    Call<ResponseRegister> registerUser(
            @Field("vsnama") String nama,
            @Field("vsalamat") String alamat,
            @Field("vsnotelp") String notelp,
            @Field("vsjenkel") String jenkel,
            @Field("vsusername") String username,
            @Field("vspassword") String password,
            @Field("vslevel") String level
    );

    @FormUrlEncoded
    @POST("loginuser.php/")
    Call<ResponseLogin> signIn(
            @Field("edtusername") String username,
            @Field("edtpassword") String password,
            @Field("vslevel") String level
    );

    @GET("kategorimakanan.php")
    Call<ResponseKategori> getDataKategori();

    @FormUrlEncoded
    @POST("getdatamakanan.php")
    Call<ResponseDataMakanan> getDataMakanan(
            @Field("vsiduser") String idUser,
            @Field("vsidkastrkategorimakanan") String idKategori
    );

}
