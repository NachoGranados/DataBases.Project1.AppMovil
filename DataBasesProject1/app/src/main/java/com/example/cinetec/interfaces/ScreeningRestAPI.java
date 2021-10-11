package com.example.cinetec.interfaces;

import com.example.cinetec.models.Screening;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScreeningRestAPI {

    @GET("screening")
    public Call<List<Screening>> getScreenings();

    @GET("screening/{id}")
    public Call<Screening> getScreening(@Path("id") String id);

}
