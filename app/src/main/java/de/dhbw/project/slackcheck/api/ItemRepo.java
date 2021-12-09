package de.dhbw.project.slackcheck.api;

import java.util.List;

import de.dhbw.project.slackcheck.pojo.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemRepo {

    private final ItemApi itemApi;

    public ItemRepo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pcheap.gitdeploy.xyz")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        itemApi = retrofit.create(ItemApi.class);
    }

    public void getItems(Callback<List<Item>> callback, String query){
        Call<List<Item>> itemApiResultCall = itemApi.getItems(query, false);
        itemApiResultCall.enqueue(callback);
    }
}
