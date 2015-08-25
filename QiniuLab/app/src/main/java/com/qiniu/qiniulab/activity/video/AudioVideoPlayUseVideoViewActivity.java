package com.qiniu.qiniulab.activity.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
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
        this.initLayout();
        this.initVideoPlay();
    }

    private void initLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.videoview_fixed_layout);
        int width = dm.widthPixels;
        int height = width * 360 / 640;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layout.setLayoutParams(layoutParams);
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
        final String videoName = this.getIntent().getStringExtra("VideoName");
        final String adsUrl = this.getIntent().getStringExtra("AdsUrl");
        final String videoUrl = this.getIntent().getStringExtra("VideoUrl");
        this.setTitle(videoName);

        final long startTime = System.currentTimeMillis();

        //common settings
        videoPlayView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoPlayLogTextView.append("Error, Pos" + mp.getCurrentPosition() + "\r\n");
                return false;
            }
        });

        if (!adsUrl.isEmpty()) {
            ///////////////// Play the ads first /////////////////////////

            videoPlayView.setVideoURI(Uri.parse(adsUrl));
            videoPlayView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    long duration = videoPlayView.getDuration();
                    long endTime = System.currentTimeMillis();
                    long loadTime = endTime - startTime;

                    videoPlayLogTextView.append("Load Ads Time: "
                            + Tools.formatMilliSeconds(loadTime) + ", Duration: " + duration + "ms\r\n");
                    mp.start();
                }
            });


            //////////////////Play the video then ////////////////////////

            //video to play
            videoPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoPlayView.setVideoURI(Uri.parse(videoUrl));
                    final long startTime2 = System.currentTimeMillis();
                    videoPlayView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            long duration = videoPlayView.getDuration();
                            long endTime = System.currentTimeMillis();
                            long loadTime = endTime - startTime2;
                            videoPlayLogTextView.append("Load Video Time: "
                                    + Tools.formatMilliSeconds(loadTime) + ", Duration:" + duration + "ms\r\n");
                            mp.start();
                        }
                    });
                    videoPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            videoPlayLogTextView.append("All Play Ends\r\n");
                        }
                    });
                }
            });
        } else {
            //video to play
            videoPlayView.setVideoURI(Uri.parse(videoUrl));
            final long startTime2 = System.currentTimeMillis();
            videoPlayView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    long duration = videoPlayView.getDuration();
                    long endTime = System.currentTimeMillis();
                    long loadTime = endTime - startTime2;
                    videoPlayLogTextView.append("Load Video Time: "
                            + Tools.formatMilliSeconds(loadTime) + ", Duration:" + duration + "ms\r\n");
                    mp.start();
                }
            });
            videoPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoPlayLogTextView.append("All Play Ends\r\n");
                }
            });
        }
    }
}
