package de.dhbw.project.pcheap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemListener {

    List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<>(Arrays.asList(
               new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65)
        ));

        ItemAdapter adapter = new ItemAdapter(items, this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rvHits);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        items.get(position);
        Intent intent = new Intent(this, Details.class);
        startActivity(intent);
    }
}