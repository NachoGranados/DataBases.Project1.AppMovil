package com.example.cinetec.interfaces;

import com.example.cinetec.models.Actor;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ActorRestAPI {

    @GET("actor")
    public Call<List<Actor>> getActors();

}
