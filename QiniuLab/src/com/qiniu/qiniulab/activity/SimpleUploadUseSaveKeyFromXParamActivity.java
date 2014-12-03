package com.qiniu.qiniulab.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.qiniu.android.http.CompletionHandler;
import com.qiniu.android.http.HttpManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.config.QiniuLabConfig;

public class SimpleUploadUseSaveKeyFromXParamActivity extends ActionBarActivity {
	private SimpleUploadUseSaveKeyFromXParamActivity context;
	private TextView uploadTokenTextView;
	private TextView uploadFileTextView;
	private EditText uploadXParamEditText;
	private TextView uploadLogTextView;
	private HttpManager httpManager;
	private UploadManager uploadManager;
	private static final int REQUEST_CODE = 8090;

	public SimpleUploadUseSaveKeyFromXParamActivity() {
		this.httpManager = new HttpManager();
		this.uploadManager = new UploadManager();
		this.context = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.simple_upload_use_save_key_from_xparam_activity);
		this.uploadTokenTextView = (TextView) this
				.findViewById(R.id.simple_upload_use_save_key_from_xparam_upload_token);
		this.uploadFileTextView = (TextView) this
				.findViewById(R.id.simple_upload_use_save_key_from_xparam_upload_file);
		this.uploadXParamEditText = (EditText) this
				.findViewById(R.id.simple_upload_use_save_key_from_xparam_file_key);
		this.uploadLogTextView = (TextView) this
				.findViewById(R.id.simple_upload_use_save_key_from_xparam_log_textview);

	}

	public void getUploadToken(View view) {
		this.httpManager.postData(QiniuLabConfig.makeUrl(
				QiniuLabConfig.REMOTE_SERVICE_SERVER,
				QiniuLabConfig.SIMPLE_UPLOAD_USE_SAVE_KEY_FROM_XPARAM),
				QiniuLabConfig.EMPTY_BODY, null, null, new CompletionHandler() {

					@Override
					public void complete(ResponseInfo respInfo,
							JSONObject jsonData) {
						if (respInfo.statusCode == 200) {
							try {
								String uploadToken = jsonData
										.getString("uptoken");
								uploadTokenTextView.setText(uploadToken);
							} catch (JSONException e) {
								Toast.makeText(
										context,
										context.getString(R.string.qiniu_get_upload_token_failed),
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(
									context,
									context.getString(R.string.qiniu_get_upload_token_failed),
									Toast.LENGTH_LONG).show();
						}
					}
				});
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
						this.uploadFileTextView.setText(path);
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
		String uploadToken = this.uploadTokenTextView.getText().toString();
		File uploadFile = new File(this.uploadFileTextView.getText().toString());
		String uploadXParam = this.uploadXParamEditText.getText().toString();
		Map<String, String> xParams = new HashMap<String, String>();
		xParams.put("x:saveKeyEx", uploadXParam);
		UploadOptions uploadOptions = new UploadOptions(xParams, null, false,
				null, null);
		this.uploadManager.put(uploadFile, null, uploadToken,
				new UpCompletionHandler() {
					@Override
					public void complete(String key, ResponseInfo respInfo,
							JSONObject jsonData) {
						if (respInfo.isOK()) {
							try {
								String fileKey = jsonData.getString("key");
								String fileHash = jsonData.getString("hash");
								String xSaveKeyEx = jsonData
										.getString("x:saveKeyEx");
								uploadLogTextView.append("File Key: " + fileKey
										+ "\r\n");

								uploadLogTextView.append("File Hash: "
										+ fileHash + "\r\n");
								uploadLogTextView.append("XParam x:saveKeyEx: "
										+ xSaveKeyEx + "\r\n");
								uploadLogTextView.append("-------\r\n");
							} catch (JSONException e) {
								Toast.makeText(
										context,
										context.getString(R.string.qiniu_upload_file_response_parse_error),
										Toast.LENGTH_LONG).show();
								uploadLogTextView.append(jsonData.toString());
								uploadLogTextView.append("\r\n");
								uploadLogTextView.append("-------\r\n");
							}
						} else {
							Toast.makeText(
									context,
									context.getString(R.string.qiniu_upload_file_failed),
									Toast.LENGTH_LONG).show();

							uploadLogTextView.append("StatusCode: "
									+ respInfo.statusCode + "\r\n");
							uploadLogTextView.append("Reqid: " + respInfo.reqId
									+ "\r\n");
							uploadLogTextView.append("Xlog: " + respInfo.xlog
									+ "\r\n");
							uploadLogTextView.append("Error: " + respInfo.error
									+ "\r\n");
							uploadLogTextView.append("-------\r\n");
						}
					}

				}, uploadOptions);
	}

}
