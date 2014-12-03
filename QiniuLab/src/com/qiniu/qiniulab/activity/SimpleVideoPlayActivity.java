package com.qiniu.qiniulab.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.qiniu.qiniulab.R;

public class SimpleVideoPlayActivity extends ActionBarActivity {
	private VideoView videoPlayView;
	private MediaController videoPlayController;

	public SimpleVideoPlayActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.simple_video_play_activity);
		this.initVideoPlay();
	}

	private void initVideoPlay() {
		this.videoPlayController = new MediaController(this);
		this.videoPlayView = (VideoView) this
				.findViewById(R.id.simple_video_play_videoview);
		videoPlayView.setMediaController(videoPlayController);
		videoPlayController.setMediaPlayer(videoPlayView);
		videoPlayController.setAnchorView(videoPlayView);
		String videoName = this.getIntent().getStringExtra("VideoName");
		String videoUrl = this.getIntent().getStringExtra("VideoUrl");
		this.setTitle(videoName);
		videoPlayView.setVideoURI(Uri.parse(videoUrl));
		videoPlayView.start();
	}
}
