package com.example.cinetec.interfaces;

import com.example.cinetec.models.Cinema;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CinemaRestAPI {

    @GET("cinema")
    public Call<List<Cinema>> getCinemas();

    @GET("cinema/{number}")
    public Call<Cinema> getCinema(@Path("number") String number);











}
