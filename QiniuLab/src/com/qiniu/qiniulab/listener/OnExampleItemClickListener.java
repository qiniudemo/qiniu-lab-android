package com.qiniu.qiniulab.listener;

import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;

import com.qiniu.qiniulab.activity.QiniuLabMainActivity;
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

			}
		} else if (groupPosition == 1) {
			// advanced upload
		} else if (groupPosition == 2) {
			// public download
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
