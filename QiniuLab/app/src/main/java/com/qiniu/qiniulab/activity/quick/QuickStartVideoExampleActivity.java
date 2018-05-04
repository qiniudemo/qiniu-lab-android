package com.qiniu.qiniulab.activity.quick;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;
import com.qiniu.qiniulab.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuickStartVideoExampleActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 8090;
    private QuickStartVideoExampleActivity context;
    private LinearLayout uploadStatusLayout;
    private ProgressBar uploadProgressBar;
    private TextView uploadSpeedTextView;
    private TextView uploadFileLengthTextView;
    private TextView uploadPercentageTextView;
    private TextView persistentIdTextView;
    private PLVideoView uploadResultVideoView;
    private UploadManager uploadManager;
    private long uploadLastTimePoint;
    private long uploadLastOffset;
    private long uploadFileLength;
    private String uploadFilePath;
    private TextView pfopResult1TextView;
    private TextView pfopResult2TextView;
    private Button loadPfopVideo1Button;
    private Button loadPfopVideo2Button;

    public QuickStartVideoExampleActivity() {
        this.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.quick_start_video_example_activity);
        this.initLayout();
        this.uploadProgressBar = (ProgressBar) this
                .findViewById(R.id.quick_start_video_upload_progressbar);
        this.uploadProgressBar.setMax(100);
        this.uploadStatusLayout = (LinearLayout) this
                .findViewById(R.id.quick_start_video_upload_status_layout);
        this.uploadSpeedTextView = (TextView) this
                .findViewById(R.id.quick_start_video_upload_speed_textview);
        this.uploadFileLengthTextView = (TextView) this
                .findViewById(R.id.quick_start_video_upload_file_length_textview);
        this.uploadPercentageTextView = (TextView) this
                .findViewById(R.id.quick_start_video_upload_percentage_textview);
        this.uploadStatusLayout.setVisibility(LinearLayout.INVISIBLE);
        this.uploadResultVideoView = (PLVideoView)
                this.findViewById(R.id.quick_start_video_play_pldplayer);
        this.persistentIdTextView = (TextView) this.findViewById(R.id.quick_start_video_pid_textview);
        this.pfopResult1TextView = (TextView) this.findViewById(R.id.quick_start_video1_textview);
        this.pfopResult2TextView = (TextView) this.findViewById(R.id.quick_start_video2_textview);
        this.loadPfopVideo1Button = (Button) this.findViewById(R.id.quick_start_load_video_button_1);
        this.loadPfopVideo2Button = (Button) this.findViewById(R.id.quick_start_load_video_button_2);
    }

    private void initLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.quick_start_pili_videoview_fixed_layout);
        int width = dm.widthPixels;
        int height = width * 360 / 640;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layout.setLayoutParams(layoutParams);
    }

    public void selectUploadFile(View view) {
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target,
                this.getString(R.string.choose_file));
        try {
            this.startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
                            this.uploadFilePath = path;
                        } catch (Exception e) {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_get_upload_file_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFile(View view) {
        if (this.uploadFilePath == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient httpClient = new OkHttpClient();
                Request req = new Request.Builder().url(QiniuLabConfig.makeUrl(
                        QiniuLabConfig.REMOTE_SERVICE_SERVER,
                        QiniuLabConfig.QUICK_START_VIDEO_DEMO_PATH)).method("GET", null).build();

                Response resp = null;
                try {
                    resp = httpClient.newCall(req).execute();
                    JSONObject jsonObject = new JSONObject(resp.body().string());
                    String uploadToken = jsonObject.getString("uptoken");
                    String domain = jsonObject.getString("domain");

                    upload(uploadToken, domain);
                } catch (Exception e) {
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_get_upload_token_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } finally {
                    if (resp != null) {
                        resp.body().close();
                    }
                }
            }
        }).start();
    }

    private void upload(final String uploadToken, final String domain) {
        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }

        File uploadFile = new File(this.uploadFilePath);
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        updateStatus(percent);
                    }
                }, null);
        final long startTime = System.currentTimeMillis();
        final long fileLength = uploadFile.length();
        this.uploadFileLength = fileLength;
        this.uploadLastTimePoint = startTime;
        this.uploadLastOffset = 0;
        // prepare status
        AsyncRun.runInMain(new Runnable() {
            @Override
            public void run() {
                //clear old status
                loadPfopVideo1Button.setVisibility(View.INVISIBLE);
                loadPfopVideo2Button.setVisibility(View.INVISIBLE);
                persistentIdTextView.setText("");

                uploadPercentageTextView.setText("0 %");
                uploadSpeedTextView.setText("0 KB/s");
                uploadFileLengthTextView.setText(Tools.formatSize(fileLength));
                uploadStatusLayout.setVisibility(LinearLayout.VISIBLE);
            }
        });

        this.uploadManager.put(uploadFile, null, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        // reset status
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                uploadStatusLayout
                                        .setVisibility(LinearLayout.INVISIBLE);
                                uploadProgressBar.setProgress(0);
                            }
                        });

                        long lastMillis = System.currentTimeMillis()
                                - startTime;
                        if (respInfo.isOK()) {
                            try {
                                String fileKey = jsonData.getString("key");
                                final String persistentId = jsonData.getString("persistentId");

                                AsyncRun.runInMain(new Runnable() {
                                    @Override
                                    public void run() {
                                        persistentIdTextView.setText(persistentId);
                                    }
                                });
                                final String videoUrl = domain + "/" + fileKey;
                                final PLVideoView videoView = uploadResultVideoView;

                                videoView.setVideoURI(Uri.parse(videoUrl));
                                videoView.setOnPreparedListener(new PLOnPreparedListener() {
                                    @Override
                                    public void onPrepared(int i) {
                                        videoView.start();
                                    }
                                });
                            } catch (JSONException e) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.qiniu_upload_file_response_parse_error),
                                        Toast.LENGTH_LONG).show();
                                Log.e(QiniuLabConfig.LOG_TAG, e.getMessage());
                            }
                        } else {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_upload_file_failed),
                                    Toast.LENGTH_LONG).show();
                            Log.e(QiniuLabConfig.LOG_TAG, respInfo.toString());
                        }
                    }

                }, uploadOptions);
    }

    private void updateStatus(final double percentage) {
        long now = System.currentTimeMillis();
        long deltaTime = now - uploadLastTimePoint;
        long currentOffset = (long) (percentage * uploadFileLength);
        long deltaSize = currentOffset - uploadLastOffset;
        if (deltaTime <= 100) {
            return;
        }

        final String speed = Tools.formatSpeed(deltaSize, deltaTime);
        // update
        uploadLastTimePoint = now;
        uploadLastOffset = currentOffset;

        AsyncRun.runInMain(new Runnable() {
            @Override
            public void run() {
                int progress = (int) (percentage * 100);
                uploadProgressBar.setProgress(progress);
                uploadPercentageTextView.setText(progress + " %");
                uploadSpeedTextView.setText(speed);
            }
        });
    }

    public void queryPfopResultButton(View view) {
        Log.d("QiniuLab", "query button clicked");
        final String persistentId = this.persistentIdTextView.getText().toString();
        if (persistentId.isEmpty()) {
            return;
        }

        final OkHttpClient httpClient = new OkHttpClient();
        final Request req = new Request.Builder().url(String.format("%s?persistentId=%s", QiniuLabConfig.makeUrl(
                QiniuLabConfig.REMOTE_SERVICE_SERVER,
                QiniuLabConfig.QUERY_PFOP_RESULT_PATH), persistentId)).method("GET", null).build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response resp = httpClient.newCall(req).execute();
                    JSONObject jsonObject = new JSONObject(resp.body().string());
                    final JSONArray keys = jsonObject.getJSONArray("keys");
                    final String videoDomain = jsonObject.getString("domain");
                    int length = keys.length();
                    if (length == 2) {
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String url1 = videoDomain + "/" + keys.getString(0);
                                    String url2 = videoDomain + "/" + keys.getString(1);
                                    pfopResult1TextView.setText(url1);
                                    pfopResult2TextView.setText(url2);
                                    loadPfopVideo1Button.setVisibility(View.VISIBLE);
                                    loadPfopVideo2Button.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    Log.e("QiniuLab", "get key from keys error");
                                }

                            }
                        });
                    } else if (length == 1) {
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String url1 = videoDomain + "/" + keys.getString(0);
                                    pfopResult1TextView.setText(url1);
                                    loadPfopVideo1Button.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    Log.e("QiniuLab", "get key from keys error");
                                }

                            }
                        });
                    } else {
                        AsyncRun.runInMain(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "no results", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AsyncRun.runInMain(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "pfop query failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }


    public void loadVideo(View view) {
        switch (view.getId()) {
            case R.id.quick_start_load_video_button_1:
                String url1 = this.pfopResult1TextView.getText().toString();
                loadVideoByUrl(url1);
                break;
            case R.id.quick_start_load_video_button_2:
                String url2 = this.pfopResult2TextView.getText().toString();
                loadVideoByUrl(url2);
                break;
        }
    }

    public void loadVideoByUrl(String url) {
        final PLVideoView videoView = uploadResultVideoView;
        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                videoView.start();
            }
        });
    }
}
