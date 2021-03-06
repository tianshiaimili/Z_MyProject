
package com.hua.adapter;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hua.bean.NewModle;
import com.hua.view.NewItemView;
import com.hua.view.NewItemView_;

@EBean
public class NewAdapter extends BaseAdapter {
    public List<NewModle> lists = new ArrayList<NewModle>();

    private String currentItem;

    public void appendList(List<NewModle> list) {
        if (!lists.containsAll(list) && list != null && list.size() > 0) {
            lists.addAll(list);
        }
        notifyDataSetChanged();
    }

    @RootContext
    Context context;

    public void clear() {
        lists.clear();
        notifyDataSetChanged();
    }

    public void currentItem(String item) {
        this.currentItem = item;
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

        NewItemView newItemView;

        if (convertView == null) {
            newItemView = NewItemView_.build(context);
//            newItemView = new NewItemView(context);
        } else {
            newItemView = (NewItemView) convertView;
        }

        NewModle newModle = lists.get(position);
        if (newModle.getImagesModle() == null) {
            newItemView.setTexts(newModle.getTitle(), newModle.getDigest(),
                    newModle.getImgsrc(), currentItem);
        } else {
            newItemView.setImages(newModle);
        }

        return newItemView;
    }
}
