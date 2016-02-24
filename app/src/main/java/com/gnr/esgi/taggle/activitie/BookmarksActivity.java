package com.gnr.esgi.taggle.activitie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.adapter.ListArticlesAdapter;
import com.gnr.esgi.taggle.constant.ArticleConstants;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.util.Config;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    List<Article> bookmarksList = new ArrayList<>();
    ListView bookmarksListView;
    ListArticlesAdapter bookmarksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Config.getTheme());
        setContentView(R.layout.activity_bookmarks);

        bookmarksListView = (ListView) findViewById(R.id.bookmarks_list);

        bookmarksAdapter = new ListArticlesAdapter(getApplicationContext(), bookmarksList);
        bookmarksListView.setAdapter(bookmarksAdapter);

        bookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openArticleDetail(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        bookmarksList = ArticleHelper.getBookmarkedArticles();

        bookmarksAdapter.swapItems(bookmarksList);
    }

    private void openArticleDetail(int position) {
        Intent intent = new Intent(this, DetailArticleActivity.class);

        intent.putExtra(
                ArticleConstants.ARTICLE_KEY_ID,
                bookmarksList.get(position).getArticleId()
        );

        startActivity(intent);
    }
}
