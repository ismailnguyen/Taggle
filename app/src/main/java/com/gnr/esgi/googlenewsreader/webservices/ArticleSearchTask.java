package com.gnr.esgi.googlenewsreader.webservices;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.gnr.esgi.googlenewsreader.GNRApplication;
import com.gnr.esgi.googlenewsreader.api.HttpRetriever;
import com.gnr.esgi.googlenewsreader.helper.ArticleHelper;
import com.gnr.esgi.googlenewsreader.models.Article;
import com.gnr.esgi.googlenewsreader.models.Tag;
import com.gnr.esgi.googlenewsreader.factory.ArticleFactory;

public class ArticleSearchTask extends AsyncTask<Tag, Tag, Tag>{

    @Override
    protected Tag doInBackground(Tag... params) {
        params[0].setArticles(
            ArticleFactory
                .createArticlesList(
                    HttpRetriever
                        .retrieveStream(
                            ArticleHelper
                                .getUrl(
                                    params[0]
                                        .getTagName()))));

        return params[0];
    }

    @Override
    protected void onPostExecute(Tag tag) {
        int added = 0;

        if(tag != null) {
            for (Tag oldTag : GNRApplication.getUser().getData().getTags()) {
                if (tag.getTagName().equals(oldTag.getTagName())) {
                    for (Article article : tag.getArticlesList()) {
                        if(!oldTag.getArticlesList().contains(article)) {

                            oldTag.getArticlesList().add(article);

                            ArticleHelper.saveArticleInDataBase(article, oldTag);

                            added++;
                        }
                    }
                }
            }
        }

        if(added > 0)
            Toast.makeText(GNRApplication.getAppContext(), String.format("%d new articles", added), Toast.LENGTH_SHORT).show();
        //Snackbar.make(GNRApplication.getAppContext(), message, Snackbar.LENGTH_LONG).show();

    }
}
