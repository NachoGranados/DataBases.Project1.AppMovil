package com.example.cinetec.interfaces;

import com.example.cinetec.models.Client;
import com.example.cinetec.models.Seat;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SeatRestAPI {

    @GET("seat/{screeningId}")
    public Call<List<Seat>> getSeats(@Path("screeningId") int screeningId);

    @PUT("seat/{screeningId}/{row_number}/{column_number}")
    public Call<Seat> putSeat(@Path("screeningId") int screeningId,
                              @Path("row_number") int row_number,
                              @Path("column_number") int column_number,
                              @Body Seat seat);

}
