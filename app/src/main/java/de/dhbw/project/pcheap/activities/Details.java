package de.dhbw.project.pcheap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.pojo.Item;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Item i = getIntent().getParcelableExtra("item");

        TextView textView;

        textView = findViewById(R.id.name);
        textView.setText(i.getName());

        textView = findViewById(R.id.price);
        textView.setText(Double.toString(i.getPrice()));

        textView = findViewById(R.id.description);
        textView.setText(i.getDescription());

        textView = findViewById(R.id.platform);
        textView.setText(i.getPlatform());

        textView = findViewById(R.id.url);
        textView.setText(i.getSiteUrl());

        ImageView imageView = findViewById(R.id.pic);
        Picasso.get().load(i.getImageUrl()).into(imageView);

        setUpGraph(i);
    }

    private void setUpGraph(Item i){
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(i.getHistory());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) {
            Toast.makeText(this, "No history available", Toast.LENGTH_SHORT).show();
            return;
        }

        GraphView graphView = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        long minDate = Long.MAX_VALUE;
        long maxDate = 0;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;
        for (int j = 0; j < jsonArray.length(); j++) {
            DataPoint dp = null;
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                Date date = new Date(jsonObject.getLong("timestamp")*1000L);
                double price = jsonObject.getDouble("price");
                minDate = Math.min(minDate, date.getTime());
                maxDate = Math.max(maxDate, date.getTime());
                minPrice = Math.min(minPrice, price);
                maxPrice = Math.max(maxPrice, price);
                dp = new DataPoint(date, price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            series.appendData(dp, true, jsonArray.length()+1);
        }

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        // set viewport to contain everything
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        double xMargin = (maxDate-minDate)/5.;
        double yMargin = (maxPrice-minPrice)/5.;
        graphView.getViewport().setMinX(minDate-xMargin);
        graphView.getViewport().setMaxX(maxDate+xMargin);
        graphView.getViewport().setMinY(minPrice-yMargin);
        graphView.getViewport().setMaxY(maxPrice+yMargin);

        graphView.addSeries(series);

        graphView.getGridLabelRenderer().setHumanRounding(false);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, sdf));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
    }
}