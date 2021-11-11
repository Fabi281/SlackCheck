package de.dhbw.project.pcheap;

public class Item {
    private String imageUrl;
    private String name;
    private double price;

    public Item(String iUrl, String Name, double Price){
        this.setImageUrl(iUrl);
        this.setName(Name);
        this.setPrice(Price);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
