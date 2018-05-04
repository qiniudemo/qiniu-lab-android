package com.qiniu.qiniulab.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AudioVideoPlayUsePLDPlayerListActivity extends ActionBarActivity {
    private Context context;
    private ListView playlistView;

    public AudioVideoPlayUsePLDPlayerListActivity() {
        this.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.public_video_play_list_activity);
        this.playlistView = (ListView) this
                .findViewById(R.id.public_video_play_list_view);
        this.playlistView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView videoName = (TextView) view
                        .findViewById(R.id.simple_video_play_list_item_name_textview);
                TextView adsUrl = (TextView) view.findViewById(R.id.simple_video_play_list_item_ads_url_textview);
                TextView videoUrl = (TextView) view
                        .findViewById(R.id.simple_video_play_list_item_video_url_textview);
                Intent intent = new Intent(context,
                        AudioVideoPlayUsePLDPlayerActivity.class);
                intent.putExtra("VideoName", videoName.getText());
                intent.putExtra("AdsUrl", adsUrl.getText());
                intent.putExtra("VideoUrl", videoUrl.getText());
                context.startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadPlaylist();
            }
        }).start();
    }


    private void loadPlaylist() {
        final OkHttpClient httpClient = new OkHttpClient();
        Request req = new Request.Builder().url(QiniuLabConfig.makeUrl(
                QiniuLabConfig.REMOTE_SERVICE_SERVER,
                QiniuLabConfig.PUBLIC_VIDEO_PLAY_LIST_PATH)).method("GET", null).build();
        Response resp = null;

        try {
            resp = httpClient.newCall(req).execute();
            JSONObject jsonObject = new JSONObject(resp.body().string());
            JSONArray playlistArray = jsonObject.getJSONArray("playlist");
            List<Map<String, String>> playlistDataList = new ArrayList<Map<String, String>>();
            for (int i = 0; i < playlistArray.length(); i++) {
                JSONObject videoObj = playlistArray.getJSONObject(i);
                String name = videoObj.getString("name");
                String adsUrl = videoObj.getString("ads_url");
                String videoUrl = videoObj.getString("video_url");
                Map<String, String> playlistData = new HashMap<String, String>();
                playlistData.put("NAME", name);
                playlistData.put("ADS_URL", adsUrl);
                playlistData.put("VIDEO_URL", videoUrl);
                playlistDataList.add(playlistData);
            }
            // pack playlist
            final SimpleAdapter playlistAdapter = new SimpleAdapter(
                    context,
                    playlistDataList,
                    R.layout.simple_video_play_list_item,
                    new String[]{"NAME", "ADS_URL", "VIDEO_URL"},
                    new int[]{
                            R.id.simple_video_play_list_item_name_textview,
                            R.id.simple_video_play_list_item_ads_url_textview,
                            R.id.simple_video_play_list_item_video_url_textview});
            AsyncRun.runInMain(new Runnable() {
                @Override
                public void run() {
                    playlistView.setAdapter(playlistAdapter);
                }
            });

        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(
                    context,
                    context.getString(R.string.qiniu_get_public_video_playlist_failed),
                    Toast.LENGTH_LONG).show();
        } finally {
            if (resp != null) {
                resp.body().close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.public_video_play_list_activity_menu,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_video_play_list_refresh_menu_item:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadPlaylist();
                    }
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
