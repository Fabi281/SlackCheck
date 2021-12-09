package de.dhbw.project.pcheap.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.adapter.ItemAdapter;
import de.dhbw.project.pcheap.api.ItemRepo;
import de.dhbw.project.pcheap.pojo.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main";
    private final ArrayList<Item> filteredItems = new ArrayList<>();
    private ItemAdapter adapter;
    private final ItemRepo ir = new ItemRepo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        adapter = new ItemAdapter(filteredItems);
        RecyclerView rv = findViewById(R.id.rvHits);
        rv.setAdapter(adapter);
        // Set to improve performance
        rv.setItemViewCacheSize(4);
        rv.setHasFixedSize(true);
    }

    private void loadData(String query){
        // Call API with Query from SearchView
        ir.getItems(new Callback<List<Item>>(){
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null){
                    // On successful response clear all items and fill it with the new items
                    filteredItems.clear();
                    filteredItems.addAll(response.body());
                    sortByPrice(true);
                    // The API call is now finished thus the Loading-Icon can be removed and
                    // the RecyclerView can be set visible
                    findViewById(R.id.loading_layout).setVisibility(View.GONE);
                    findViewById(R.id.rvHits).setVisibility(View.VISIBLE);
                }else{
                    // On failure we set the RecyclerView and Loading-Icon invisible if they
                    // aren't already and present the User with an ErrorScreen
                    Log.d(TAG, "onResponse: fail");
                    findViewById(R.id.rvHits).setVisibility(View.GONE);
                    findViewById(R.id.loading_layout).setVisibility(View.GONE);
                    findViewById(R.id.viewNoResults).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                // On failure we set the RecyclerView and Loading-Icon invisible if they
                // aren't already and present the User with an ErrorScreen
                findViewById(R.id.rvHits).setVisibility(View.GONE);
                findViewById(R.id.loading_layout).setVisibility(View.GONE);
                findViewById(R.id.viewNoResults).setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: fail" + t);
            }
        }, query);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate Menu and set Query Listener for searchView
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Set Loading-Icon visible, everything else invisible
                findViewById(R.id.rvHits).setVisibility(View.GONE);
                findViewById(R.id.viewNoSearch).setVisibility(View.GONE);
                findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
                // Start Loading the data from Backend-API and close the SearchView (UX)
                loadData(query);
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
        // Sort the Recyclerview based on the filter the user selected
        // No Switch-Statement is used due to ids not being final and Android Studio itself
        // suggesting not to use Switch + ids
        if(item.getItemId() == R.id.SortAlpha)
            sortByName();
        if(item.getItemId() == R.id.SortPriceInc)
            sortByPrice(true);
        if(item.getItemId() == R.id.SortPriceDec)
            sortByPrice(false);
        if(item.getItemId() == R.id.SortGrowthInc)
            sortByGrowth(true);
        if(item.getItemId() == R.id.SortGrowthDec)
            sortByGrowth(false);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortByName()
    {
        Comparator<Item> itemComparatorByName = Comparator.comparing(Item::getName);
        filteredItems.sort(itemComparatorByName);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortByPrice(boolean increasing)
    {
        Comparator<Item> itemComparatorByPrice;
        if(increasing){
            itemComparatorByPrice = Comparator.comparingDouble(Item::getPrice);
        }else{
            itemComparatorByPrice = Comparator.comparingDouble(Item::getPrice).reversed();
        }
        filteredItems.sort(itemComparatorByPrice);
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void sortByGrowth(boolean increasing)
    {
        Comparator<Item> itemComparatorByGrowth;
        if(increasing){
            itemComparatorByGrowth = Comparator.comparingDouble(Item::getGrowth);
        }else{
            itemComparatorByGrowth = Comparator.comparingDouble(Item::getGrowth).reversed();
        }
        filteredItems.sort(itemComparatorByGrowth);
        adapter.notifyDataSetChanged();
    }
}