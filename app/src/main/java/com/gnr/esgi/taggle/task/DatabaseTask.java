package com.gnr.esgi.taggle.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.gnr.esgi.taggle.activitie.HomeActivity;
import com.gnr.esgi.taggle.adapter.ListArticlesAdapter;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.model.Article;
import java.util.List;

public class DatabaseTask extends AsyncTask<Void, List<Article>, List<Article>>{

    private ListArticlesAdapter adapter;
    private ProgressDialog progressDialog;

    public DatabaseTask(ListArticlesAdapter adapter,
                        ProgressDialog progressDialog) {
        this.adapter = adapter;
        this.progressDialog = progressDialog;
    }

    @Override
    protected List<Article> doInBackground(Void... params) {
        List<Article> articles = ArticleHelper.getArticles();

        ArticleHelper.sortByDate(articles);

        return articles;
    }

    @Override
    protected void onPostExecute(List<Article> articles) {
        if(!articles.isEmpty()) {
            HomeActivity.articlesList = articles;
            
            adapter.swapItems(articles);

            // Show "Load more" button only if articles list already have items
            HomeActivity.loadMore.setVisibility(
                    articles.size() > 0
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if(progressDialog != null) {
            progressDialog.dismiss();

            progressDialog = null;
        }
    }
}
