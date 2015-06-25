package com.qiniu.qiniulab.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.qiniu.qiniulab.R;

import java.util.List;

public class ExampleExpandableListAdapter extends BaseExpandableListAdapter {
    private List<String> exampleGroupTitleList;
    private List<List<String>> exampleItemTitleList;
    private LayoutInflater inflater;

    public ExampleExpandableListAdapter(Context context,
                                        List<String> exampleGroupTitleList,
                                        List<List<String>> exampleItemTitleList) {
        this.inflater = ((Activity) context).getLayoutInflater();
        this.exampleGroupTitleList = exampleGroupTitleList;
        this.exampleItemTitleList = exampleItemTitleList;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return this.exampleItemTitleList.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 32 + childPosition;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        TextView sectionItemTextView;
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.example_item_list_item, parent, false);
        }
        sectionItemTextView = (TextView) convertView
                .findViewById(R.id.section_item_list_item);
        sectionItemTextView.setText(this.exampleItemTitleList
                .get(groupPosition).get(childPosition));
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return this.exampleItemTitleList.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return this.exampleGroupTitleList.get(groupPosition);
    }

    public int getGroupCount() {
        return this.exampleGroupTitleList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition * 32;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView sectionNameView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.example_group_list_item,
                    parent, false);
        }
        sectionNameView = (TextView) convertView
                .findViewById(R.id.section_list_item);
        sectionNameView.setText(this.exampleGroupTitleList.get(groupPosition));
        return sectionNameView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
