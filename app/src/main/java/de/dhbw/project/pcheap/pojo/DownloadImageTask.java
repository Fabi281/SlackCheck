package de.dhbw.project.pcheap.pojo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask implements Runnable{

    private final ImageView bmImage;
    private final String url;

    public DownloadImageTask(ImageView imageView, String Url){
        this.bmImage = imageView;
        this.url = Url;
    }

    @Override
    public void run() {

        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        bmImage.setImageBitmap(mIcon);
    }
}
