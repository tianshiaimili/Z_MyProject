
package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hua.view.NewImageView;
import com.hua.view.NewImageView_;

@EBean
public class ImageAdapter extends BaseAdapter {

    public List<String> lists = new ArrayList<String>();

    @RootContext
    Context context;

    public void appendList(List<String> list) {
        if (!lists.containsAll(list) && list != null && list.size() > 0) {
            lists.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewImageView newImageView;
        if (convertView == null) {
            newImageView = NewImageView_.build(context);
        } else {
            newImageView = (NewImageView) convertView;
        }

        newImageView.setImage(lists, position);

        return newImageView;
    }

}
