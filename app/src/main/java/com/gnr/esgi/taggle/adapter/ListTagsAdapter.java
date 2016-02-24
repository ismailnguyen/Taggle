package com.gnr.esgi.taggle.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.model.Tag;
import java.util.List;

public class ListTagsAdapter extends BaseAdapter {

    private Context context;
    private List<Tag> tagsList;
    private SparseBooleanArray selectedItems;

    public ListTagsAdapter(Context context, List<Tag> tags) {
        this.context = context;
        this.tagsList = tags;
        this.selectedItems = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return tagsList.size();
    }

    @Override
    public Tag getItem(int position) {
        return tagsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(Tag tag) {
        tagsList.remove(tag);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        tagsList.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tag_setting_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tag tagSelected = tagsList.get(position);
        viewHolder.name.setText(tagSelected.getName());

        return convertView;
    }

    private class ViewHolder {
        TextView name;
    }

    public void swapItems(List<Tag> items) {
        this.tagsList = items;
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItems.get(position));
    }

    public void removeSelection() {
        selectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if(value) {
            selectedItems.put(position, value);
        }
        else {
            selectedItems.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItems.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItems;
    }
}
