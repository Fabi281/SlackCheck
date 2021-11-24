package de.dhbw.project.pcheap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.dhbw.project.pcheap.pojo.Item;
import de.dhbw.project.pcheap.adapter.ItemAdapter;
import de.dhbw.project.pcheap.api.ItemRepo;
import de.dhbw.project.pcheap.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";
    private List<Item> items = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();
    private ItemAdapter adapter;
    private RecyclerView rv;
    private ItemRepo ir = new ItemRepo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        adapter = new ItemAdapter(filteredItems);
        rv = findViewById(R.id.rvHits);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData(String query, boolean newItems){
        ir.getItems(new Callback<List<Item>>(){
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    filteredItems.addAll(response.body());
                    items = filteredItems;
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "onResponse: fail");
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.d(TAG, "onFailure: fail" + t.toString());
            }
        }, query, newItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Data here...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadData(query, false);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return true; }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.SortAlph:
                sortByName();
                break;
            case R.id.SortPrice:
                sortByPrice();
                break;
        }
        return true;
    }

    private void sortByName()
    {
        Comparator<Item> itemComparatorByName = new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getName().compareTo(item2.getName());
            }
        };
        Collections.sort(filteredItems, itemComparatorByName);
        adapter.notifyDataSetChanged();
    }

    private void sortByPrice()
    {
        Comparator<Item> itemComparatorByPrice = new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return Double.compare(item1.getPrice(), item2.getPrice());
            }
        };
        Collections.sort(filteredItems, itemComparatorByPrice);
        adapter.notifyDataSetChanged();
    }
}