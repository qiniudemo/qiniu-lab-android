package com.qiniu.qiniulab.activity.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;
import com.qiniu.qiniulab.utils.DomainUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SimpleImageViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_image_view_activity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                basicImageView();
            }
        }).start();
    }

    private void basicImageView() {
        List<String> imageUrls = this.getImageUrls();
        final GridView gridView = (GridView) this.findViewById(R.id.simple_grid_image_view);
        ArrayList<HashMap<String, Object>> viewData = new ArrayList<HashMap<String, Object>>();
        for (String imageUrl : imageUrls) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("image", imageUrl);
            viewData.add(item);
        }
        final SimpleAdapter adapter = new SimpleAdapter(this, viewData,
                R.layout.simple_image_view_item,
                new String[]{"image"},
                new int[]{R.id.image});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView && data instanceof String) {
                    final OkHttpClient httpClient = new OkHttpClient();
                    final String imageUrl = data.toString();

                    final URI imageUri = URI.create(imageUrl);
                    final String host = imageUri.getHost();

                    final ImageView imageView = (ImageView) view;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String reqImageUrl = imageUrl;
                            Response response = null;
                            try {
                                String ip = DomainUtils.getIpByDomain(host);
                                if (ip != null) {
                                    reqImageUrl = String.format("%s://%s%s", imageUri.getScheme(), ip, imageUri.getPath());
                                    if (imageUri.getQuery() != null) {
                                        reqImageUrl = String.format("%s?%s", reqImageUrl, imageUri.getQuery());
                                    }
                                }
                                final Request.Builder builder = new Request.Builder();
                                Request request = builder.url(reqImageUrl).addHeader("Host", host).method("GET", null).build();
                                response = httpClient.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    byte[] bytes = response.body().bytes();
                                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    AsyncRun.runInMain(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageBitmap(bitmap);
                                        }
                                    });
                                }
                            } catch (IOException e) {

                            } finally {
                                if (response != null) {
                                    response.body().close();
                                }
                            }
                        }
                    }).start();

                    return true;
                } else {
                    return false;
                }
            }
        });
        AsyncRun.runInMain(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(adapter);
            }
        });
    }

    private List<String> getImageUrls() {
        final List<String> imageUrls = new ArrayList<String>();
        final OkHttpClient httpClient = new OkHttpClient();
        //获取设备的分辨率，以从服务器获取合适大小的图片显示
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Request req = new Request.Builder().url(QiniuLabConfig.makeUrl(
                QiniuLabConfig.REMOTE_SERVICE_SERVER,
                QiniuLabConfig.PUBLIC_IMAGE_VIEW_LIST_PATH) + "?device_width=" + dm.widthPixels).method("GET", null).build();
        Response resp = null;
        try {
            resp = httpClient.newCall(req).execute();
            if (resp.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(resp.body().string());
                JSONArray imagesArray = jsonObject.getJSONArray("images");
                int count = imagesArray.length();
                for (int i = 0; i < count; i++) {
                    imageUrls.add(imagesArray.getString(i));
                }

            }
        } catch (Exception ex) {

        } finally {
            if (resp != null) {
                resp.body().close();
            }
        }
        return imageUrls;
    }

}
