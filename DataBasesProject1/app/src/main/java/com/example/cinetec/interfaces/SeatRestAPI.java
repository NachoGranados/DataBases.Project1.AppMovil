package com.example.cinetec.interfaces;

import com.example.cinetec.models.Screening;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SeatRestAPI {

    @GET("seat/{screeningId}")
    public Call<Screening> getScreening(@Path("screeningId") String screeningId);

}
