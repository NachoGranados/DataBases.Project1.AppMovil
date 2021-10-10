package com.example.cinetec.interfaces;

import com.example.cinetec.models.Client;
import com.example.cinetec.models.MovieTheater;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieTheaterRestAPI {

    @GET("movietheater")
    public Call<List<MovieTheater>> getMovieTheaters();

    @GET("movietheater/{name}")
    public Call<MovieTheater> getMovieTheater(@Path("name") String name);

}
