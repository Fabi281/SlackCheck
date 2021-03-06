package de.dhbw.project.slackcheck.activities;

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

import de.dhbw.project.slackcheck.R;
import de.dhbw.project.slackcheck.databinding.ActivityDetailsBinding;
import de.dhbw.project.slackcheck.pojo.Item;

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

        // Use DataBinding to bind the data to the layout
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
                        // Get the difference between the start of the fling/swipe and end to get
                        // the distance and direction
                        float xDiff = e2.getX() - e1.getX();

                        // Swipe to the next/previous Item in the list based on swipe-direction.
                        // Check if a certain distance and velocity threshold has been met to
                        // prevent that some taps are accidentally counted as swipes
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
                        return false;
                    }
                };
        gestureDetector = new GestureDetector(this, listener);
    }

    public void onShopButtonClicked(View view) {
        // Go to the Online-Shop where the Item is sold
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(android.net.Uri.parse(item.getSiteUrl()));
        startActivity(intent);
    }

    private void showMessageNoGraph() {
        // Show a message that we have not enough data to set up a graph
        // (or are not allowed to store data)
        findViewById(R.id.graph).setVisibility(View.GONE);
        findViewById(R.id.graph_trend_img).setVisibility(View.GONE);
        findViewById(R.id.txt_no_history).setVisibility(View.VISIBLE);
    }

    private void setUpGraph(Item i) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(i.getHistory());
        } catch (JSONException e) {
            e.printStackTrace();
            // No error for the user here, as they will see a message instead of the graph anyway.
        }

        if (jsonArray == null || jsonArray.length() < 2) {
            showMessageNoGraph();
            return;
        }

        GraphView graphView = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        // We set the viewport of the graph manually, to override default behaviour.
        // Loop is for determining the minimum and maximum values of the graph,
        // as well as creating datapoints for the series.
        long minDate = Long.MAX_VALUE;
        long maxDate = 0;
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;
        for (int j = 0; j < jsonArray.length(); j++) {
            DataPoint dp;
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
                showMessageNoGraph();
                return;
            }
            series.appendData(dp, true, jsonArray.length() + 1);
        }

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        // When a datapoint is clicked, we show the price of that datapoint in a Toast.
        series.setOnDataPointTapListener(
                (series1, dataPoint) -> Toast.makeText(getApplicationContext(),
                        String.format(Locale.getDefault(), getString(R.string.formatted_price), dataPoint.getY()),
                        Toast.LENGTH_SHORT).show());

        // "margin" for the growth value to accommodate floating point issues.
        // (The accumulated growth will never be exactly 1.0, so we allow for some error.)
        if (i.getGrowth() < 0.99)
            series.setColor(getColor(R.color.trend_good));
        else if (i.getGrowth() > 1.01)
            series.setColor(getColor(R.color.trend_bad));
        else
            series.setColor(getColor(R.color.trend_neutral));

        graphView.addSeries(series);

        // set viewport to contain everything plus some padding
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        double xMargin = (maxDate - minDate) / 5.;
        double yMargin = (maxPrice - minPrice) / 5.;
        graphView.getViewport().setMinX(minDate - xMargin);
        graphView.getViewport().setMaxX(maxDate + xMargin);
        graphView.getViewport().setMinY(minPrice - yMargin);
        graphView.getViewport().setMaxY(maxPrice + yMargin);

        // HumanRounding shouldn't be used for dates, as it messes up the graph,
        // because it rounds the values ignorant of them being timestamps.
        graphView.getGridLabelRenderer().setHumanRounding(false, true);
        // Display the timestamps in a human-readable format.
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, sdf));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);

        // Same margins as above
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

        // List and Position to be able to Swipe to previous/next result in the next Activity as well
        in.putParcelableArrayListExtra("itemList", itemList);
        in.putExtra("position", currentPosition + direction);

        // Set this flag so swiping does not interact with the Back-Button
        in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(in);

        // Start slide-animation based on swipe direction
        if (direction < 0)
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
        // If there are any animations playing right now, cancel them to prevent weird interactions
        if (animator != null) {
            animator.cancel();
        }

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
        // Start animation
        set.start();
        animator = set;
    }

    // set up the animator for the image
    private AnimatorSet getImageAnimator(boolean doOpen) {

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        smallImageView.getGlobalVisibleRect(startBounds);
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

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y)
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
        // Start the animation faster and end slower
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