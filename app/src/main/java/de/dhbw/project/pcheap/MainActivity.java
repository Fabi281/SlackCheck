package de.dhbw.project.pcheap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List items = new ArrayList<>(Arrays.asList(
               new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65),
                new Item("https://m.media-amazon.com/images/I/910YTH4gSCL._AC_SY450_.jpg", "RTX2070", 922.65)
        ));

        ItemAdapter adapter = new ItemAdapter(items);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rvHits);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}