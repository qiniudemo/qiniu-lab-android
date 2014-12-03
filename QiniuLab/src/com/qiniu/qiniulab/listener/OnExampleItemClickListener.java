package com.qiniu.qiniulab.listener;

import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;

import com.qiniu.qiniulab.activity.PublicVideoPlayListActivity;
import com.qiniu.qiniulab.activity.QiniuLabMainActivity;
import com.qiniu.qiniulab.activity.SimpleUploadUseSaveKeyActivity;
import com.qiniu.qiniulab.activity.SimpleUploadWithKeyActivity;
import com.qiniu.qiniulab.activity.SimpleUploadWithoutKeyActivity;

public class OnExampleItemClickListener implements
		ExpandableListView.OnChildClickListener {
	private QiniuLabMainActivity mainActivity;

	public OnExampleItemClickListener(QiniuLabMainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Intent intent = null;
		boolean result = false;
		if (groupPosition == 0) {
			// simple upload
			if (childPosition == 0) {
				intent = new Intent(this.mainActivity,
						SimpleUploadWithoutKeyActivity.class);
			} else if (childPosition == 1) {
				intent = new Intent(this.mainActivity,
						SimpleUploadWithKeyActivity.class);
			} else if (childPosition == 2) {
				intent = new Intent(this.mainActivity,
						SimpleUploadUseSaveKeyActivity.class);
			}
		} else if (groupPosition == 1) {
			// advanced upload
		} else if (groupPosition == 2) {
			// public download
			if (childPosition == 2) {
				intent = new Intent(this.mainActivity,
						PublicVideoPlayListActivity.class);
			}
		} else if (groupPosition == 3) {
			// private download
		}
		if (intent != null) {
			this.mainActivity.startActivity(intent);
			result = true;
		}
		return result;

	}

}
