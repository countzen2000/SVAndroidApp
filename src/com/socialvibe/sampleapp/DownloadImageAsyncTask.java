/**
 * The DownloadImageAsyncTask class asynchronously downloads an image from a web URL, stores it in
 * the application's cache folder, decodes it to a bitmap, and loads it into a provided ImageView.
 */

package com.socialvibe.sampleapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class DownloadImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    
    private Context context;
    private ImageView view;
    private String urlSpec;
    private Animation scrollUpAnimation;
    
    public DownloadImageAsyncTask(Context context, ImageView view, String urlSpec) {
        this.context = context;
        this.view = view;
        this.urlSpec = urlSpec;
        scrollUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scroll_up_animation);
        scrollUpAnimation.setInterpolator(new DecelerateInterpolator(5.0f));
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        
        if (view != null) {
            InputStream is = null;
            FileOutputStream fos = null;
            
            try {
                // Download image to cache directory.
                String filename = URLEncoder.encode(urlSpec, "utf-8");
                File file = new File(context.getCacheDir(), filename);
                
                URL url = new URL(urlSpec);
                URLConnection connection = url.openConnection();
                is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }
                
                fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                
                // Decode the downloaded image.
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[16*1024];
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                    fos.close();
                } catch (Exception e) {
                }
            }
        } 
        
        return bitmap;
    }
    
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // If a bitmap was downloaded and decoded successfully, load it into the
        // provided ImageView.
        if (view != null && bitmap != null) {
            view.setImageBitmap(bitmap);
            
        // Else, for the purpose of this sample app, supply a placeholder image in case 
        // the engagement didn't return a valid image URL.  You generally don't need
        // to handle this scenario in your own app.
        } else {
            view.setImageResource(R.drawable.banner_placeholder);
        }
        
        // Start the animation to make the image visible.
        view.setVisibility(View.VISIBLE);
        view.startAnimation(scrollUpAnimation);
    }
}
