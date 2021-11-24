package de.dhbw.project.pcheap.api;

import java.util.List;

import de.dhbw.project.pcheap.pojo.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemRepo {

    private ItemApi itemApi;

    public ItemRepo(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pcheap.gitdeploy.xyz")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        itemApi = retrofit.create(ItemApi.class);
    }

    public void getItems(Callback<List<Item>> callback, String query, boolean newItems){
        Call<List<Item>> itemApiResultCall = itemApi.getItems(query, newItems);
        itemApiResultCall.enqueue(callback);
    }
}
