package com.qiniu.qiniulab.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.qiniu.qiniulab.R;
import com.qiniu.qiniulab.adapter.ExampleExpandableListAdapter;
import com.qiniu.qiniulab.listener.OnExampleItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QiniuLabMainActivity extends ActionBarActivity {

    private ExpandableListView exampleListView;
    private List<String> exampleGroupTitleList;
    private List<List<String>> exampleItemTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qiniu_lab_main_activity);
        this.exampleGroupTitleList = new ArrayList<String>();
        this.exampleItemTitleList = new ArrayList<List<String>>();
        this.exampleListView = (ExpandableListView) this
                .findViewById(R.id.example_expandable_listview);
        this.inflateExpandableListView();

    }

    private void inflateExpandableListView() {
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_quick_start));
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_simple_upload));
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_advanced_upload));
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_audio_video_play));
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_image_view));
        this.exampleGroupTitleList.add(this
                .getString(R.string.qiniu_system_capture));

        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_quick_start_demo)));
        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_simple_upload_values)));
        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_advanced_upload_values)));
        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_video_play)));
        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_image_view)));
        this.exampleItemTitleList.add(Arrays.asList(this.getResources()
                .getStringArray(R.array.qiniu_system_capture)));
        ExpandableListAdapter exampleListViewAdapter = new ExampleExpandableListAdapter(
                this, this.exampleGroupTitleList, this.exampleItemTitleList);
        this.exampleListView.setAdapter(exampleListViewAdapter);
        OnExampleItemClickListener onExampleItemClickListener = new OnExampleItemClickListener(
                this);
        this.exampleListView
                .setOnChildClickListener(onExampleItemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qiniu_lab_main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent aboutIntent = new Intent(QiniuLabMainActivity.this,
                        AboutActivity.class);
                this.startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
