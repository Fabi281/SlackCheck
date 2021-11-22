package de.dhbw.project.pcheap.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("history")
    @Expose
    private Object[] history;


    public Item(String Name, double Price, String desc, String plat, String sUrl,
                String iUrl, Object[] history) {
        this.setImageUrl(iUrl);
        this.setName(Name);
        this.setPrice(Price);
        this.setSiteUrl(sUrl);
        this.setDescription(desc);
        this.setPlatform(plat);
        this.setHistory(history);
    }

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
    }
}