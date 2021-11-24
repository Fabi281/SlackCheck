package de.dhbw.project.pcheap.api;

import java.util.List;

import de.dhbw.project.pcheap.pojo.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItemApi {

    @GET("/?")
    Call<List<Item>> getItems(@Query("query") String query, @Query("load_new") boolean loadnew);

}
