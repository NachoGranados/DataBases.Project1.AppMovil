package com.example.cinetec.interfaces;

import com.example.cinetec.models.Seat;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SeatRestAPI {

    @GET("seat/{screeningId}")
    public Call<List<Seat>> getSeats(@Path("screeningId") int screeningId);

}
