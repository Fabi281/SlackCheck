package de.dhbw.project.pcheap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;


import java.text.SimpleDateFormat;
import java.util.Date;

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

        GraphView graphView = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();


        for (int j = 0; j < jsonArray.length(); j++) {
            DataPoint dp = null;
            try {
                dp = new DataPoint(
                        new Date((long) (jsonArray.getJSONObject(j)
                                .getDouble("timestamp") * 1000L)),
                        jsonArray.getJSONObject(j)
                                .getDouble("price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            series.appendData(dp, true, jsonArray.length()+1);
        }

        graphView.addSeries(series);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, sdf));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(4);
    }
}