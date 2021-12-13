package de.dhbw.project.slackcheck.api;

import java.util.List;

import de.dhbw.project.slackcheck.pojo.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Simplified interface for calling the SlackCheck API vie Retrofit.
 */
public class ItemRepo {

    private final ItemApi itemApi;

    public ItemRepo(){
        // Build Retrofit model to be able to call the API later on
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slackcheck.gitdeploy.xyz")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        itemApi = retrofit.create(ItemApi.class);
    }

    public void getItems(Callback<List<Item>> callback, String query){
        // Start the API-Call with the SearchView query and load_new=false to load data from cache
        // as every call costs us money. If we were to release the App we would need to set it
        // to true for it to fully work as intended (aka its a developer-tool)
        Call<List<Item>> itemApiResultCall = itemApi.getItems(query, false);
        itemApiResultCall.enqueue(callback);
    }
}
