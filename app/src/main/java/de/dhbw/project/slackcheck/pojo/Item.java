package de.dhbw.project.slackcheck.pojo;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import de.dhbw.project.slackcheck.R;

/**
 * POJO for the Item.
 * Simple data-class for the items.
 * Set up to be populated by the JSON-response from the API.
 * Containing utility methods for data-binding.
 */
public class Item implements Parcelable {
    @SerializedName("image")
    @Expose
    private String imageUrl;

    @SerializedName("url")
    @Expose
    private String siteUrl;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("growth")
    @Expose
    private double growth;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("history")
    @Expose
    private Object[] history;


    public String getSiteUrl() { return siteUrl; }

    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }

    public String getDescription() { return description; }

    public void setDescription(String description) {this.description = description; }

    public String getPlatform() { return platform; }

    public void setPlatform(String platform) { this.platform = platform; }

    public Object[] getHistory() { return history; }

    public void setHistory(Object[] history) { this.history = history; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String url) { this.imageUrl = url; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public double getGrowth() { return growth; }

    public void setGrowth(double growth) { this.growth = growth; }

    // Methods to be able to pass it in Intents

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getImageUrl());
        parcel.writeString(getSiteUrl());
        parcel.writeString(getName());
        parcel.writeDouble(getPrice());
        parcel.writeString(getDescription());
        parcel.writeString(getPlatform());
        parcel.writeArray(getHistory());
        parcel.writeDouble(getGrowth());
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public Item(Parcel in) {
        this.setImageUrl(in.readString());
        this.setSiteUrl(in.readString());
        this.setName(in.readString());
        this.setPrice(in.readDouble());
        this.setDescription(in.readString());
        this.setPlatform(in.readString());
        this.setHistory(in.readArray(Object.class.getClassLoader()));
        this.setGrowth(in.readDouble());
    }

    // Methods for data binding

    /**
     * Custom behaviour for the imageUrl parameter.
     * If a Item is bound to a layout and the imageUrl is set, this handler will take care of loading the image using Glide.
     * @param view The ImageView to load the image into.
     * @param imageUrl The string specified in the imageUrl parameter.
     */
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext()).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(view.getContext(), R.string.ImageError, Toast.LENGTH_SHORT).show();
                if (e != null)
                    e.printStackTrace();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(view);
    }

    /**
     * Returns a formatted string of the growth value.
     * Positive numbers: +x%
     * Negative numbers: -x%
     * Zero: +0%
     *
     * Used in data binding as item.growthFormatted.
     * @return A formatted string of the growth value.
     */
    public String getGrowthFormatted(){
        double growthInPercent = (getGrowth()-1)*100;
        String formatted = String.format(Locale.getDefault(), "%.2f", growthInPercent) + "%";
        if (growthInPercent > 0.1) {
            return "+" + formatted;
        } else if (growthInPercent < -0.1){
            return formatted;
        } else
            return "+0%";
    }
}
