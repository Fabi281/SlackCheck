package de.dhbw.project.slackcheck.api;

import java.util.List;

import de.dhbw.project.slackcheck.pojo.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItemApi {

    // The API caches calls in order to reduce costly calls to different data-providers.
    // load_new is used to force loading new data.
    // While never set to true in the app, it's set to false to make sure the cache is used.
    // It's set to true only in manual calls for populating the history.
    // In a production environment this parameter wouldn't exist, rather would the backend handle this intelligently.
    @GET("/?")
    Call<List<Item>> getItems(@Query("query") String query, @Query("load_new") boolean loadNew);

}
