package com.gnr.esgi.taggle.model;

import android.database.Cursor;
import android.util.Log;
import com.gnr.esgi.taggle.constant.ArticleConstants;
import com.gnr.esgi.taggle.constant.DatabaseConstants;
import com.gnr.esgi.taggle.util.Config;
import org.json.JSONException;
import org.json.JSONObject;

public class Article {

    private Long articleId;
    private Boolean read;
    private Boolean deleted;
    private Boolean bookmarked;

    private String title;
    private String content;
    private String createdAt;
    private Picture picture;
    private Source source;
    private String linkTagName;

    public Article() {
    }

    public Article(JSONObject jsonArticle) {
        try {
            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_TITLE))
                title = jsonArticle.getString(
                            ArticleConstants.ARTICLE_KEY_TITLE
                );

            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_CONTENT))
                content = jsonArticle.getString(
                            ArticleConstants.ARTICLE_KEY_CONTENT
                );

            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_CREATED_AT))
                createdAt = jsonArticle.getString(
                                ArticleConstants.ARTICLE_KEY_CREATED_AT
                );

            source = new Source();

            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_SOURCE_NAME))
                source.setName(
                        jsonArticle.getString(
                                ArticleConstants.ARTICLE_KEY_SOURCE_NAME
                        )
                );

            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_SOURCE_URL))
                source.setUrl(
                        jsonArticle.getString(
                                ArticleConstants.ARTICLE_KEY_SOURCE_URL
                        )
                );

            picture = new Picture();
            if(jsonArticle.has(ArticleConstants.ARTICLE_KEY_PICTURE))
            {
                JSONObject jsonImage = jsonArticle.getJSONObject(
                        ArticleConstants.ARTICLE_KEY_PICTURE
                );

                if(jsonImage.has(ArticleConstants.ARTICLE_KEY_PICTURE_URL))
                    picture.setPictureUrl(
                            jsonImage.getString(
                                    ArticleConstants.ARTICLE_KEY_PICTURE_URL
                            )
                    );
            }

            read = false;
            deleted = false;
            bookmarked = false;
        }
        catch (JSONException e) {
            if(Config.DISPLAY_LOG)
                Log.d("Article", e.getMessage());
        }

        if(Config.DISPLAY_LOG)
            Log.d(ArticleConstants.TAG, "New Article(JSONObject c):" + this.toString());
    }

    public Article(Cursor cursor) {
        articleId = cursor.getLong(
                        cursor.getColumnIndex(
                            DatabaseConstants.ArticleEntry._ID
                        )
        );

        title = cursor.getString(
                cursor.getColumnIndex(
                        DatabaseConstants.ArticleEntry.COLUMN_TITLE
                )
        );

        content = cursor.getString(
                    cursor.getColumnIndex(
                        DatabaseConstants.ArticleEntry.COLUMN_CONTENT
                    )
        );

        createdAt = cursor.getString(
                        cursor.getColumnIndex(
                            DatabaseConstants.ArticleEntry.COLUMN_DATE
                        )
        );

        source = new Source();
        source.setName(
                cursor.getString(
                        cursor.getColumnIndex(
                                DatabaseConstants.ArticleEntry.COLUMN_SOURCE_NAME
                        )
                )
        );

        source.setUrl(
                cursor.getString(
                        cursor.getColumnIndex(
                                DatabaseConstants.ArticleEntry.COLUMN_SOURCE_URL
                        )
                )
        );

        picture = new Picture(
                    cursor.getString(
                        cursor.getColumnIndex(
                            DatabaseConstants.ArticleEntry.COLUMN_PICTURE_URL
                        )
                    )
        );

        read = cursor.getInt(
                cursor.getColumnIndex(
                    DatabaseConstants.ArticleEntry.COLUMN_READ
                )
        ) == 1;

        deleted = cursor.getInt(
                cursor.getColumnIndex(
                        DatabaseConstants.ArticleEntry.COLUMN_DELETED
                )
        ) == 1;

        bookmarked = cursor.getInt(
                cursor.getColumnIndex(
                        DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED
                )
        ) == 1;

        linkTagName = cursor.getString(
                        cursor.getColumnIndex(
                            DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME
                        )
        );
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long id) {
        articleId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getLinkTagName() {
        return linkTagName;
    }

    public void setLinkTagName(String tagName) {
        linkTagName = tagName;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}
