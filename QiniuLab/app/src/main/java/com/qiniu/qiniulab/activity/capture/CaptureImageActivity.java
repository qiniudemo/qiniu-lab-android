package com.qiniu.qiniulab.activity.capture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;
import com.qiniu.qiniulab.utils.DomainUtils;
import com.qiniu.qiniulab.utils.Tools;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureImageActivity extends ActionBarActivity {
    private CaptureImageActivity context;
    private final int CAPTURE_IMAGE_CODE = 8090;
    private LinearLayout uploadStatusLayout;
    private ProgressBar uploadProgressBar;
    private TextView uploadSpeedTextView;
    private TextView uploadFileLengthTextView;
    private TextView uploadPercentageTextView;
    private ImageView uploadResultImageView;
    private UploadManager uploadManager;
    private long uploadLastTimePoint;
    private long uploadLastOffset;
    private long uploadFileLength;
    private String uploadFilePath;
    private Button uploadFileButton;
    private TextView captureImageFilePathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image_activity);
        this.context = this;

        this.uploadProgressBar = (ProgressBar) this
                .findViewById(R.id.capture_image_upload_progressbar);
        this.uploadProgressBar.setMax(100);
        this.uploadStatusLayout = (LinearLayout) this
                .findViewById(R.id.capture_image_upload_status_layout);
        this.uploadSpeedTextView = (TextView) this
                .findViewById(R.id.capture_image_upload_speed_textview);
        this.uploadFileLengthTextView = (TextView) this
                .findViewById(R.id.capture_image_upload_file_length_textview);
        this.uploadPercentageTextView = (TextView) this
                .findViewById(R.id.capture_image_upload_percentage_textview);
        this.uploadStatusLayout.setVisibility(LinearLayout.INVISIBLE);
        this.uploadResultImageView = (ImageView) this.findViewById(R.id.capture_image_view);
        this.uploadFileButton = (Button) this.findViewById(R.id.capture_image_upload_button);
        this.uploadFileButton.setEnabled(false);
        this.captureImageFilePathTextView = (TextView) this.findViewById(R.id.capture_image_file_path_textview);
    }

    public void captureImage(View view) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File picFile = null;
            try {
                picFile = createImageFile();
            } catch (Exception ex) {
                Log.e("QiniuLab", "create file " + this.uploadFilePath + " failed");
            }

            if (picFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
                this.startActivityForResult(intent, CAPTURE_IMAGE_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Log.d("QiniuLab", "resquest code is " + requestCode + ", result code is " + resultCode);

        switch (requestCode) {
            case CAPTURE_IMAGE_CODE:
                this.uploadFileButton.setEnabled(true);
                this.captureImageFilePathTextView.setText(this.uploadFilePath);
                break;
        }
    }


    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String imageFileName = "PIC-" + timestamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        this.uploadFilePath = image.getAbsolutePath();
        return image;
    }

    public void uploadFile(View view) {
        if (this.uploadFilePath == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncHttpClient httpClient = new SyncHttpClient();
                httpClient.get(QiniuLabConfig.makeUrl(
                                QiniuLabConfig.REMOTE_SERVICE_SERVER,
                                QiniuLabConfig.QUICK_START_IMAGE_DEMO_PATH),
                        null, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                try {
                                    String uploadToken = response.getString("uptoken");
                                    String domain = response.getString("domain");

                                    upload(uploadToken, domain);
                                } catch (JSONException e) {
                                    AsyncRun.run(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(
                                                    context,
                                                    context.getString(R.string.qiniu_get_upload_token_failed),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                final String msg = context.getString(R.string.qiniu_get_upload_token_failed) + "\r\nStatusCode:"
                                        + statusCode + "\r\n" + throwable.toString() + "\r\n";
                                AsyncRun.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        });
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
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
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
                        AsyncRun.run(new Runnable() {
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
                                DisplayMetrics dm = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(dm);
                                final int width = dm.widthPixels;
                                final SyncHttpClient httpClient = new SyncHttpClient();
                                final String imageUrl = domain + "/" + fileKey + "?imageView2/0/w/" + width + "/format/jpg";

                                final URI imageUri = URI.create(imageUrl);
                                final String host = imageUri.getHost();

                                final ImageView imageView = uploadResultImageView;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
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

                                        httpClient.get(reqImageUrl, new BinaryHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                AsyncRun.run(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        imageView.setImageBitmap(bitmap);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

                                            }
                                        });

                                    }
                                }).start();
                            } catch (JSONException e) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.qiniu_upload_file_response_parse_error),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_upload_file_failed),
                                    Toast.LENGTH_LONG).show();
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

        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                int progress = (int) (percentage * 100);
                uploadProgressBar.setProgress(progress);
                uploadPercentageTextView.setText(progress + " %");
                uploadSpeedTextView.setText(speed);
            }
        });
    }
}
