package com.qiniu.qiniulab.listener;

import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;

import com.qiniu.qiniulab.activity.QiniuLabMainActivity;
import com.qiniu.qiniulab.activity.capture.CaptureImageActivity;
import com.qiniu.qiniulab.activity.capture.CaptureVideoActivity;
import com.qiniu.qiniulab.activity.image.SimpleImageViewActivity;
import com.qiniu.qiniulab.activity.quick.QuickStartImageExampleActivity;
import com.qiniu.qiniulab.activity.quick.QuickStartVideoExampleActivity;
import com.qiniu.qiniulab.activity.upload.CallbackUploadWithKeyInJsonFormatActivity;
import com.qiniu.qiniulab.activity.upload.CallbackUploadWithKeyInUrlFormatActivity;
import com.qiniu.qiniulab.activity.upload.ResumableUploadWithKeyActivity;
import com.qiniu.qiniulab.activity.upload.ResumableUploadWithoutKeyActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadEnableCrc32CheckActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadOverwriteExistingFileActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseEndUserActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseFsizeLimitActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseMimeLimitActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseReturnBodyActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseSaveKeyActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadUseSaveKeyFromXParamActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadWithKeyActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadWithMimeTypeActivity;
import com.qiniu.qiniulab.activity.upload.SimpleUploadWithoutKeyActivity;
import com.qiniu.qiniulab.activity.video.AudioVideoPlayUsePLDPlayerListActivity;
import com.qiniu.qiniulab.activity.video.AudioVideoPlayUseVideoViewListActivity;

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
            //quick start
            if (childPosition == 0) {
                intent = new Intent(this.mainActivity, QuickStartImageExampleActivity.class);
            } else if (childPosition == 1) {
                intent = new Intent(this.mainActivity, QuickStartVideoExampleActivity.class);
            }
        } else if (groupPosition == 1) {
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
            } else if (childPosition == 3) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadUseSaveKeyFromXParamActivity.class);
            } else if (childPosition == 4) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadUseReturnBodyActivity.class);
            } else if (childPosition == 5) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadOverwriteExistingFileActivity.class);
            } else if (childPosition == 6) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadUseFsizeLimitActivity.class);
            } else if (childPosition == 7) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadUseMimeLimitActivity.class);
            } else if (childPosition == 8) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadWithMimeTypeActivity.class);
            } else if (childPosition == 9) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadEnableCrc32CheckActivity.class);
            } else if (childPosition == 10) {
                intent = new Intent(this.mainActivity,
                        SimpleUploadUseEndUserActivity.class);
            }
        } else if (groupPosition == 2) {
            // advanced upload
            if (childPosition == 0) {
                intent = new Intent(this.mainActivity,
                        ResumableUploadWithoutKeyActivity.class);
            } else if (childPosition == 1) {
                intent = new Intent(this.mainActivity,
                        ResumableUploadWithKeyActivity.class);
            } else if (childPosition == 2) {
                intent = new Intent(this.mainActivity,
                        CallbackUploadWithKeyInUrlFormatActivity.class);
            } else if (childPosition == 3) {
                intent = new Intent(this.mainActivity,
                        CallbackUploadWithKeyInJsonFormatActivity.class);
            }
        } else if (groupPosition == 3) {
            // audio video play
            if (childPosition == 0) {
                intent = new Intent(this.mainActivity,
                        AudioVideoPlayUseVideoViewListActivity.class);
            } else if (childPosition == 1) {
                intent = new Intent(this.mainActivity,
                        AudioVideoPlayUsePLDPlayerListActivity.class);
            }
        } else if (groupPosition == 4) {
            //image view
            if (childPosition == 0) {
                intent = new Intent(this.mainActivity, SimpleImageViewActivity.class);
            }
        } else if (groupPosition == 5) {
            //system capture
            if (childPosition == 0) {
                intent = new Intent(this.mainActivity, CaptureImageActivity.class);
            } else if (childPosition == 1) {
                intent = new Intent(this.mainActivity, CaptureVideoActivity.class);
            }
        }
        if (intent != null) {
            this.mainActivity.startActivity(intent);
            result = true;
        }
        return result;

    }

}
