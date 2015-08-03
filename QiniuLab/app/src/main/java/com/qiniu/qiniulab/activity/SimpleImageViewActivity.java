package com.qiniu.qiniulab.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.utils.DomainUtils;

import org.apache.http.Header;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SimpleImageViewActivity extends ActionBarActivity {
    private List<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_image_view_activity);
        this.populateUrls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                basicImageView();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void basicImageView() {
        List<Bitmap> bitmaps = loadImages(this.imageUrls);
        final GridView gridView = (GridView) this.findViewById(R.id.simple_grid_image_view);
        ArrayList<HashMap<String, Object>> viewData = new ArrayList<HashMap<String, Object>>();
        for (Bitmap bitmap : bitmaps) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("image",bitmap);
            viewData.add(item);
        }
        final SimpleAdapter adapter = new SimpleAdapter(this, viewData,
                R.layout.simple_image_view_item,
                new String[]{"image"},
                new int[]{R.id.image});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView imageView=(ImageView)view;
                    imageView.setImageBitmap((Bitmap)data);
                    return true;
                }else{
                    return false;
                }
            }
        });
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(adapter);
            }
        });
    }

    private void populateUrls() {
        this.imageUrls = new ArrayList<String>();
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip1.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip2.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip3.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip4.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip5.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip6.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip7.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip8.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip9.jpg");
        this.imageUrls.add("http://7u2fo5.com1.z0.glb.clouddn.com/chanyouji/trip10.jpg");

    }

    private List<Bitmap> loadImages(List<String> imageUrls) {
        final List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        SyncHttpClient httpClient = new SyncHttpClient();
        for (String imageUrl : imageUrls) {
            URI imageUri = URI.create(imageUrl);
            String host = imageUri.getHost();
            String reqImageUrl = imageUrl;
            try {
                String ip = DomainUtils.getIpByDomain(host);
                if (ip != null) {
                    reqImageUrl = String.format("%s://%s%s", imageUri.getScheme(), ip, imageUri.getPath());
                    if (imageUri.getQuery() != null) {
                        reqImageUrl = String.format("%s?%s", reqImageUrl, imageUri.getQuery());
                    }
                    httpClient.removeHeader("Host");
                    httpClient.addHeader("Host", host);
                }
            } catch (IOException e) {

            }
            System.out.println(reqImageUrl);
            httpClient.get(reqImageUrl, new BinaryHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmaps.add(bitmap);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });
        }

        return bitmaps;
    }
}
