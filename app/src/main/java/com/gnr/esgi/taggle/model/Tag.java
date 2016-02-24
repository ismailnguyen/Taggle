package com.gnr.esgi.taggle.model;

import android.database.Cursor;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.constant.DatabaseConstants;
import java.util.ArrayList;
import java.util.List;

public class Tag {

    private String name;
    private List<Article> articlesList;

    public Tag(String name) {
        this.name = name;
        articlesList = new ArrayList<>();
    }

    public Tag(List<Article> articles) {
        name = Taggle.getAppContext().getString(R.string.topic_headlines);
        articlesList = articles;
    }

    public Tag(Cursor cursor) {
        name = cursor.getString(
                    cursor.getColumnIndex(
                        DatabaseConstants.TagEntry.COLUMN_NAME
                    )
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Article> getArticlesList() {
        return articlesList;
    }

    public void setArticles(List<Article> articles) {
        articlesList = articles;
    }
}
