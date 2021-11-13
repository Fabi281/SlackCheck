package de.dhbw.project.pcheap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.pojo.Item;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Item i = getIntent().getParcelableExtra("item");

        TextView textView;

        textView = (TextView) findViewById(R.id.name);
        textView.setText(i.getName());

        textView = (TextView) findViewById(R.id.price);
        textView.setText(Double.toString(i.getPrice()));

        textView = (TextView) findViewById(R.id.description);
        textView.setText(i.getDescription());

        textView = (TextView) findViewById(R.id.platform);
        textView.setText(i.getPlatform());

        textView = (TextView) findViewById(R.id.url);
        textView.setText(i.getSiteUrl());

        ImageView imageView = findViewById(R.id.pic);
        Picasso.get().load(i.getImageUrl()).into(imageView);
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(i.getHistory());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        textView = (TextView) findViewById(R.id.growth);
        try {
            textView.setText(jsonArray.getJSONObject(0).get("growth").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        textView = (TextView) findViewById(R.id.timestamp);
        try {
            textView.setText(jsonArray.getJSONObject(0).get("timestamp").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}