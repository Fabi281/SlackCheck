package de.dhbw.project.pcheap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ItemApi {

    @GET("/")
    Call<List<Item>> getItems();

}
