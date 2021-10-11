package com.example.cinetec.interfaces;

import com.example.cinetec.models.Movie;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MovieRestAPI {

    @GET("movie")
    public Call<List<Movie>> getMovies();

    @GET("movie/{originalName}")
    public Call<Movie> getMovie(@Path("originalName") String originalName);

}
