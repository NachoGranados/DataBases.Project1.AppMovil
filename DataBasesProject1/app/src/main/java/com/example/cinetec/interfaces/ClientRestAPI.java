package com.example.cinetec.interfaces;

import com.example.cinetec.models.Client;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ClientRestAPI {

    @POST("client")
    public Call<Client> postClient(@Body Client client);

    @GET("client")
    public Call<List<Client>> getClients();

}
