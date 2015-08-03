package com.qiniu.qiniulab.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.qiniu.android.http.CompletionHandler;
import com.qiniu.android.http.HttpManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;
import com.qiniu.qiniulab.utils.Tools;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SimpleUploadOverwriteExistingFileActivity extends
        ActionBarActivity {
    private static final int REQUEST_CODE = 8090;
    private SimpleUploadOverwriteExistingFileActivity context;
    private EditText uploadFileKeyEditText;
    private TextView uploadLogTextView;
    private LinearLayout uploadStatusLayout;
    private ProgressBar uploadProgressBar;
    private TextView uploadSpeedTextView;
    private TextView uploadFileLengthTextView;
    private TextView uploadPercentageTextView;
    private HttpManager httpManager;
    private UploadManager uploadManager;
    private long uploadLastTimePoint;
    private long uploadLastOffset;
    private long uploadFileLength;
    private String uploadFilePath;

    public SimpleUploadOverwriteExistingFileActivity() {
        this.context = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.simple_upload_overwrite_existing_file_activity);
        this.uploadFileKeyEditText = (EditText) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_key);
        this.uploadProgressBar = (ProgressBar) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_progressbar);
        this.uploadProgressBar.setMax(100);
        this.uploadStatusLayout = (LinearLayout) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_status_layout);
        this.uploadSpeedTextView = (TextView) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_speed_textview);
        this.uploadFileLengthTextView = (TextView) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_file_length_textview);
        this.uploadPercentageTextView = (TextView) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_percentage_textview);
        this.uploadStatusLayout.setVisibility(LinearLayout.INVISIBLE);
        this.uploadLogTextView = (TextView) this
                .findViewById(R.id.simple_upload_overwrite_existing_file_log_textview);

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
                            this.clearLog();
                            this.writeLog(context
                                    .getString(R.string.qiniu_select_upload_file)
                                    + path);
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
        if (this.httpManager == null) {
            this.httpManager = new HttpManager();
        }
        // get upload token with key
        final String uploadFileKey = this.uploadFileKeyEditText.getText()
                .toString().trim();
        if (uploadFileKey.length() == 0) {
            Toast.makeText(context,
                    context.getString(R.string.qiniu_specify_the_file_key),
                    Toast.LENGTH_LONG).show();
            return;
        }
        byte[] postData = QiniuLabConfig.EMPTY_BODY;
        try {
            postData = new String("key=" + uploadFileKey).getBytes("utf-8");
        } catch (UnsupportedEncodingException e1) {
            Log.e("SystemError", "no supported charset utf-8");
        }
        Header contentTypeHeader = new BasicHeader("Content-Type",
                "application/x-www-form-urlencoded");
        this.httpManager.postData(QiniuLabConfig.makeUrl(
                        QiniuLabConfig.REMOTE_SERVICE_SERVER,
                        QiniuLabConfig.SIMPLE_UPLOAD_OVERWRITE_EXISTING_FILE_PATH),
                postData, 0, postData.length, new Header[]{contentTypeHeader}, null,
                new CompletionHandler() {

                    @Override
                    public void complete(ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        if (respInfo.statusCode == 200) {
                            try {
                                String uploadToken = jsonData
                                        .getString("uptoken");
                                writeLog(context
                                        .getString(R.string.qiniu_get_upload_token)
                                        + uploadToken);
                                upload(uploadFileKey, uploadToken);
                            } catch (JSONException e) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.qiniu_get_upload_token_failed),
                                        Toast.LENGTH_LONG).show();
                                writeLog(context
                                        .getString(R.string.qiniu_get_upload_token_failed)
                                        + respInfo.toString());
                                if (jsonData != null) {
                                    writeLog(jsonData.toString());
                                }
                            }
                        } else {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_get_upload_token_failed),
                                    Toast.LENGTH_LONG).show();
                            writeLog(context
                                    .getString(R.string.qiniu_get_upload_token_failed)
                                    + respInfo.toString());
                            if (jsonData != null) {
                                writeLog(jsonData.toString());
                            }

                        }
                    }
                }, null, false);
    }

    private void upload(String uploadFileKey, String uploadToken) {
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
        uploadPercentageTextView.setText("0 %");
        uploadSpeedTextView.setText("0 KB/s");
        uploadFileLengthTextView.setText(Tools.formatSize(fileLength));
        uploadStatusLayout.setVisibility(LinearLayout.VISIBLE);
        writeLog(context.getString(R.string.qiniu_upload_file) + "...");
        this.uploadManager.put(uploadFile, uploadFileKey, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        // reset status
                        uploadStatusLayout
                                .setVisibility(LinearLayout.INVISIBLE);
                        uploadProgressBar.setProgress(0);
                        long lastMillis = System.currentTimeMillis()
                                - startTime;
                        if (respInfo.isOK()) {
                            try {
                                String fileKey = jsonData.getString("key");
                                String fileHash = jsonData.getString("hash");
                                writeLog("File Size: "
                                        + Tools.formatSize(uploadFileLength));
                                writeLog("File Key: " + fileKey);
                                writeLog("File Hash: " + fileHash);
                                writeLog("Last Time: "
                                        + Tools.formatMilliSeconds(lastMillis));
                                writeLog("Average Speed: "
                                        + Tools.formatSpeed(fileLength,
                                        lastMillis));
                                writeLog("X-Reqid: " + respInfo.reqId);
                                writeLog("X-Log: " + respInfo.xlog);
                                writeLog("X-Via: " + respInfo.xvia);
                                writeLog("--------------------------------");
                            } catch (JSONException e) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.qiniu_upload_file_response_parse_error),
                                        Toast.LENGTH_LONG).show();
                                writeLog(context
                                        .getString(R.string.qiniu_upload_file_response_parse_error));
                                if (jsonData != null) {
                                    writeLog(jsonData.toString());
                                }
                                writeLog("--------------------------------");
                            }
                        } else {
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.qiniu_upload_file_failed),
                                    Toast.LENGTH_LONG).show();
                            writeLog(respInfo.toString());
                            if (jsonData != null) {
                                writeLog(jsonData.toString());
                            }
                            writeLog("--------------------------------");
                        }
                    }

                }, uploadOptions);
    }

    private synchronized void updateStatus(final double percentage) {
        long now = System.currentTimeMillis();
        long deltaTime = now - uploadLastTimePoint;
        long currentOffset = (long) (percentage * uploadFileLength);
        long deltaSize = currentOffset - uploadLastOffset;
        if (deltaTime <= 0) {
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

    private void clearLog() {
        this.uploadLogTextView.setText("");
    }

    private void writeLog(String msg) {
        this.uploadLogTextView.append(msg);
        this.uploadLogTextView.append("\r\n");
    }
}
