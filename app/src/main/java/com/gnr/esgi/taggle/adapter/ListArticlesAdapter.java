package com.gnr.esgi.taggle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.util.DateUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class ListArticlesAdapter extends BaseAdapter {

    private Context context;
    private List<Article> articlesList;
    private SparseBooleanArray selectedItems;

    public ListArticlesAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articlesList = articles;
        this.selectedItems = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return articlesList.size();
    }

    @Override
    public Article getItem(int position) {
        return articlesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Article> getItems() {
        return articlesList;
    }

    public void remove(Article article) {
        articlesList.remove(article);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        articlesList.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.content = (TextView) convertView.findViewById(R.id.news_content);
            viewHolder.createdAt = (TextView) convertView.findViewById(R.id.news_date);
            viewHolder.source = (TextView) convertView.findViewById(R.id.news_source);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.news_picture);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.picture.setVisibility(View.INVISIBLE);
        Article articleSelected = articlesList.get(position);
        viewHolder.title.setText(articleSelected.getTitle());
        viewHolder.content.setText(articleSelected.getContent());
        viewHolder.createdAt.setText(DateUtil.getDuration(articleSelected.getCreatedAt()));
        viewHolder.source.setText(articleSelected.getSource().getName());

        viewHolder.title.setTypeface(
                null,
                articleSelected.getRead()
                        ? Typeface.NORMAL
                        : Typeface.BOLD
        );

        viewHolder.createdAt.setTypeface(
                null,
                articleSelected.getRead()
                        ? Typeface.NORMAL
                        : Typeface.BOLD
        );

        viewHolder.source.setTypeface(
                null,
                articleSelected.getRead()
                        ? Typeface.NORMAL
                        : Typeface.BOLD
        );

        //Picasso.with(context).load(articleSelected.getPictureUrl()).into(viewHolder.picture);

        ImageLoader.getInstance().displayImage(articleSelected.getPicture().getPictureUrl(), viewHolder.picture, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                viewHolder.picture.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView content;
        TextView createdAt;
        TextView source;
        ImageView picture;
    }

    public void swapItems(List<Article> items) {
        this.articlesList = items;

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
