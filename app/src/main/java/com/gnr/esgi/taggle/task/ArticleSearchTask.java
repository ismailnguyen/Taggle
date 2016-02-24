package com.gnr.esgi.taggle.task;

import android.os.AsyncTask;
import com.gnr.esgi.taggle.api.HttpRetriever;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.model.Tag;
import com.gnr.esgi.taggle.factory.ArticleFactory;

public class ArticleSearchTask extends AsyncTask<Tag, Tag, Tag>{

    private Integer page;

    public ArticleSearchTask() {
        this(0);
    }

    public ArticleSearchTask(Integer page) {
        this.page = page;
    }

    @Override
    protected Tag doInBackground(Tag... params) {
        if(params.length > 0
                && params[0] != null) {
            params[0].setArticles(
                    ArticleFactory.fromJson(
                            HttpRetriever.retrieveStream(
                                ArticleHelper.getUrl(params[0].getName(), page)
                            )
                    )
            );

            return params[0];
        }
        else {
            return new Tag(
                    ArticleFactory.fromJson(
                            HttpRetriever.retrieveStream(
                                    ArticleHelper.getUrl(page)
                            )
                    )
            );
        }
    }

    @Override
    protected void onPostExecute(Tag tag) {
        if(tag != null) {
            for (Article article : tag.getArticlesList()) {
                article.setLinkTagName(tag.getName());

                ArticleHelper.saveInDataBase(article);
            }
        }
    }
}
