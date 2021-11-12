package de.dhbw.project.pcheap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";
    private List<Item> items = new ArrayList<>();
    private ItemAdapter adapter;
    private RecyclerView rv;
    private ItemRepo ir = new ItemRepo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        loadData();

    }

    private void setupRecyclerView(){
        adapter = new ItemAdapter(items);
        rv = findViewById(R.id.rvHits);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData(){
        ir.getItems(new Callback<List<Item>>(){

            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    items.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "onResponse: fail");
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "onFailure: fail" + t.toString());
            }
        });
    }
}