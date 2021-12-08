package de.dhbw.project.pcheap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.dhbw.project.pcheap.R;
import de.dhbw.project.pcheap.databinding.ActivityDetailsBinding;
import de.dhbw.project.pcheap.pojo.Item;

public class Details extends AppCompatActivity {

    ArrayList<Item> itemList;
    int currentPosition;
    Item item;
    private Animator animator;
    private int animationDuration;
    ImageView largeImageView;
    ImageView smallImageView;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        itemList = getIntent().getParcelableArrayListExtra("itemList");
        currentPosition = getIntent().getIntExtra("position", 0);
        item = itemList.get(currentPosition);

        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setItem(item);

        setUpGraph(item);

        animationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        largeImageView = findViewById(R.id.expanded_image);
        smallImageView = findViewById(R.id.pic);

        int threshold = 100;
        int velocity_threshold = 100;

        GestureDetector.SimpleOnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        float xDiff = e2.getX() - e1.getX();
                        try {
                            if (Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold) {
                                if (xDiff > 0) {
                                    if (currentPosition - 1 >= 0) {
                                        swipeToOtherItem(-1);
                                        return true;
                                    }
                                } else {
                                    if (currentPosition + 1 < itemList.size()) {
                                        swipeToOtherItem(1);
                                        return true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                };
        gestureDetector = new GestureDetector(this, listener);
    }

    public void onShopButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(item.getSiteUrl()));
        startActivity(intent);
    }

    private void setUpGraph(Item i) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(i.getHistory());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) {
            return;
        }

        if (jsonArray.length() < 2){
            findViewById(R.id.graph).setVisibility(View.GONE);
            findViewById(R.id.graph_trend_img).setVisibility(View.GONE);
            findViewById(R.id.txt_no_history).setVisibility(View.VISIBLE);
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
                Date date = new Date(jsonObject.getLong("timestamp") * 1000L);
                double price = jsonObject.getDouble("price");
                minDate = Math.min(minDate, date.getTime());
                maxDate = Math.max(maxDate, date.getTime());
                minPrice = Math.min(minPrice, price);
                maxPrice = Math.max(maxPrice, price);
                dp = new DataPoint(date, price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            series.appendData(dp, true, jsonArray.length() + 1);
        }

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        series.setOnDataPointTapListener(
                (series1, dataPoint) -> Toast.makeText(getApplicationContext(),
                        String.format(Locale.getDefault(), getString(R.string.formatted_price), dataPoint.getY()),
                        Toast.LENGTH_SHORT).show());

        if (i.getGrowth() < 0.99)
            series.setColor(getColor(R.color.trend_good));
        else if (i.getGrowth() > 1.01)
            series.setColor(getColor(R.color.trend_bad));
        else
            series.setColor(getColor(R.color.trend_neutral));

        graphView.addSeries(series);

        // set viewport to contain everything
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        double xMargin = (maxDate - minDate) / 5.;
        double yMargin = (maxPrice - minPrice) / 5.;
        graphView.getViewport().setMinX(minDate - xMargin);
        graphView.getViewport().setMaxX(maxDate + xMargin);
        graphView.getViewport().setMinY(minPrice - yMargin);
        graphView.getViewport().setMaxY(maxPrice + yMargin);

        graphView.getGridLabelRenderer().setHumanRounding(false, true);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, sdf));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);

        ImageView trendIndicator = findViewById(R.id.graph_trend_img);
        if (i.getGrowth() < 0.99)
            trendIndicator.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_down));
        else if (i.getGrowth() > 1.01)
            trendIndicator.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_up));
        else
            trendIndicator.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.arrow_flat));

    }

    private void swipeToOtherItem(int direction) {
        Intent in = new Intent(this, Details.class);
        in.putParcelableArrayListExtra("itemList", itemList);
        in.putExtra("position", currentPosition+direction);
        in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(in);
        if(direction < 0)
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_left);
        else
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_right);
    }

    public void onSmallImageClicked(View v) {
        // If there are any animations playing right now, cancel them to prevent weird interactions
        if (animator != null) {
            animator.cancel();
        }

        //Make the small imageView invisible and expanded imageView visible
        smallImageView.setVisibility(View.INVISIBLE);
        largeImageView.setVisibility(View.VISIBLE);

        AnimatorSet set = getImageAnimator(true);

        set.start();
        animator = set;
    }

    public void onLargeImageClicked(View v) {
        closeLargeImage();
    }

    private void closeLargeImage() {
        if (animator != null) {
            animator.cancel();
        }

        ImageView smallImageView = findViewById(R.id.pic);

        AnimatorSet set = getImageAnimator(false);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                smallImageView.setVisibility(View.VISIBLE);
                largeImageView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                smallImageView.setVisibility(View.VISIBLE);
                largeImageView.setVisibility(View.GONE);
            }
        });
        set.start();
        animator = set;
    }

    // set up the animator for the image
    private AnimatorSet getImageAnimator(boolean doOpen) {
        ImageView largeImageView = findViewById(R.id.expanded_image);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        findViewById(R.id.pic).getGlobalVisibleRect(startBounds);
        findViewById(R.id.DetailLayout).getGlobalVisibleRect(finalBounds, globalOffset);

        //Needed to counteract the offset otherwise the background wouldn't fill the entire screen
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        // Set Start bounds to the fit the aspect ratio using
        // the center-crop technique to prevent stretching
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        AnimatorSet set = new AnimatorSet();
        if (doOpen)
            set
                    .play(ObjectAnimator.ofFloat(largeImageView, View.X,
                            startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(largeImageView, View.Y,
                            startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(largeImageView, View.SCALE_X,
                            startScale, 1f))
                    .with(ObjectAnimator.ofFloat(largeImageView,
                            View.SCALE_Y, startScale, 1f));

        else
            set
                    .play(ObjectAnimator.ofFloat(largeImageView,
                            View.X, startBounds.left))
                    .with(ObjectAnimator.ofFloat(largeImageView,
                            View.Y, startBounds.top))
                    .with(ObjectAnimator.ofFloat(largeImageView,
                            View.SCALE_X, startScale))
                    .with(ObjectAnimator.ofFloat(largeImageView,
                            View.SCALE_Y, startScale));
        set.setDuration(animationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animator = null;
            }
        });


        return set;
    }

    @Override
    public void onBackPressed() {
        // Zoom out if the enlarged image is visible and finish the Activity if it is not
        if (findViewById(R.id.expanded_image).getVisibility() == View.VISIBLE) {
            closeLargeImage();
        } else {
            this.finish();
        }
    }

    // used for swiping between items
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}