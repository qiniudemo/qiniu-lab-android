package com.qiniu.qiniulab.activity.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.utils.Tools;

public class AudioVideoPlayUseVideoViewActivity extends ActionBarActivity {
    private VideoView videoPlayView;
    private MediaController videoPlayController;
    private TextView videoPlayLogTextView;

    public AudioVideoPlayUseVideoViewActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.simple_video_play_use_videoview_activity);
        this.initVideoPlay();
    }

    private void initVideoPlay() {
        this.videoPlayController = new MediaController(this);
        this.videoPlayView = (VideoView) this
                .findViewById(R.id.simple_video_play_videoview);
        this.videoPlayLogTextView = (TextView) this
                .findViewById(R.id.simple_video_play_log_textview);
        videoPlayView.setMediaController(videoPlayController);
        videoPlayController.setMediaPlayer(videoPlayView);
        videoPlayController.setAnchorView(videoPlayView);
        String videoName = this.getIntent().getStringExtra("VideoName");
        String videoUrl = this.getIntent().getStringExtra("VideoUrl");
        this.setTitle(videoName);
        videoPlayView.setVideoURI(Uri.parse(videoUrl));
        final long startTime = System.currentTimeMillis();
        videoPlayView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                long endTime = System.currentTimeMillis();
                long loadTime = endTime - startTime;
                videoPlayLogTextView.append("Load Time: "
                        + Tools.formatMilliSeconds(loadTime) + "\r\n");
                mp.start();
            }
        });

    }
}
