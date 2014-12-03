package com.qiniu.qiniulab.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.qiniu.android.http.CompletionHandler;
import com.qiniu.android.http.HttpManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;

public class PublicVideoPlayListActivity extends ActionBarActivity {
	private Context context;
	private HttpManager httpManager;
	private ListView playlistView;

	public PublicVideoPlayListActivity() {
		this.httpManager = new HttpManager();
		this.context = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
				TextView videoUrl = (TextView) view
						.findViewById(R.id.simple_video_play_list_item_url_textview);
				Intent intent = new Intent(context,
						SimpleVideoPlayActivity.class);
				intent.putExtra("VideoName", videoName.getText());
				intent.putExtra("VideoUrl", videoUrl.getText());
				context.startActivity(intent);
			}
		});
		this.loadPlaylist();
	}

	private void loadPlaylist() {
		this.httpManager.postData(QiniuLabConfig.makeUrl(
				QiniuLabConfig.REMOTE_SERVICE_SERVER,
				QiniuLabConfig.PUBLIC_VIDEO_PLAY_LIST_PATH),
				QiniuLabConfig.EMPTY_BODY, null, null, new CompletionHandler() {

					@Override
					public void complete(ResponseInfo respInfo,
							JSONObject jsonData) {
						if (respInfo.statusCode == 200) {
							try {
								JSONArray playlistArray = jsonData
										.getJSONArray("playlist");
								List<Map<String, String>> playlistDataList = new ArrayList<Map<String, String>>();
								for (int i = 0; i < playlistArray.length(); i++) {
									JSONObject videoObj = playlistArray
											.getJSONObject(i);
									String name = videoObj.getString("name");
									String url = videoObj.getString("url");
									Map<String, String> playlistData = new HashMap<String, String>();
									playlistData.put("NAME", name);
									playlistData.put("URL", url);
									playlistDataList.add(playlistData);
								}
								// pack playlist
								SimpleAdapter playlistAdapter = new SimpleAdapter(
										context,
										playlistDataList,
										R.layout.simple_video_play_list_item,
										new String[] { "NAME", "URL" },
										new int[] {
												R.id.simple_video_play_list_item_name_textview,
												R.id.simple_video_play_list_item_url_textview });
								playlistView.setAdapter(playlistAdapter);
							} catch (JSONException e) {
								Toast.makeText(
										context,
										context.getString(R.string.qiniu_get_public_video_playlist_failed),
										Toast.LENGTH_LONG).show();
							}
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.public_video_play_list_activity_menu,
				menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.public_video_play_list_refresh_menu_item:
			this.loadPlaylist();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
