package com.gnr.esgi.taggle.activitie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.constant.ArticleConstants;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.util.Config;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class DetailArticleActivity extends AppCompatActivity {

    TextView titleDetailArticle;
    TextView contentDetailArticle;
    TextView createdAtDetailArticle;
    ImageView pictureDetailArticle;
    TextView sourceDetailArticle;
    TextView viewOnlineDetailArticle;
    FloatingActionButton shareDetailArticle;
    FloatingActionButton bookmarkDetailArticle;

    private Article article;

    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove notification bar
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        super.onCreate(savedInstanceState);
        setTheme(Config.getTheme());
        setContentView(R.layout.activity_detail_article);

        titleDetailArticle = (TextView) findViewById(R.id.detail_article_title);
        contentDetailArticle = (TextView) findViewById(R.id.detail_article_content);
        createdAtDetailArticle = (TextView) findViewById(R.id.detail_article_created_at);
        pictureDetailArticle = (ImageView) findViewById(R.id.detail_article_picture);
        sourceDetailArticle = (TextView) findViewById(R.id.detail_article_source_name);
        viewOnlineDetailArticle = (TextView) findViewById(R.id.detail_article_view_online);
        shareDetailArticle = (FloatingActionButton) findViewById(R.id.detail_article_button_share);
        bookmarkDetailArticle = (FloatingActionButton) findViewById(R.id.detail_article_button_bookmark);

        mImageLoader = ImageLoader.getInstance();

        viewOnlineDetailArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOnline();
            }
        });

        shareDetailArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareArticle();
            }
        });

        bookmarkDetailArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarkArticle();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get article from database using article id given by previous activity
        article = ArticleHelper.getArticle(
                getIntent().getLongExtra(
                        ArticleConstants.ARTICLE_KEY_ID,
                        0
                )
        );

        updateBookmarkButton();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Don't show "View online" link if there is no url
        if(article.getSource().getUrl().isEmpty())
            viewOnlineDetailArticle.setVisibility(View.INVISIBLE);

        showDetailArticle();
    }

    public void showDetailArticle() {
        mImageLoader.displayImage(article.getPicture().getPictureUrl(), pictureDetailArticle);
        titleDetailArticle.setText(article.getTitle());
        contentDetailArticle.setText(article.getContent());
        createdAtDetailArticle.setText(article.getCreatedAt());
        sourceDetailArticle.setText(article.getSource().getName());

        Picasso.with(this).load(article.getPicture().getPictureUrl()).into(pictureDetailArticle);
    }

    public void showOnline() {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra(ArticleConstants.ARTICLE_KEY_SOURCE_URL, article.getSource().getUrl());

        startActivity(webViewIntent);
    }

    public void shareArticle() {
        String content =
                new StringBuilder()
                    .append(article.getTitle())
                    .append("\n\n")
                    .append(article.getContent())
                    .toString();

        Intent share =
                new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, content);

        startActivity(
                Intent.createChooser(
                        share,
                        article.getTitle()
                )
        );
    }

    public void bookmarkArticle() {
        // Update database, if already bookmarked, remove bookmarking, else bookmark
        article.setBookmarked(!article.getBookmarked());

        Taggle.getDao().bookmarkArticle(article);

        updateBookmarkButton();
    }

    public void updateBookmarkButton() {
        bookmarkDetailArticle.setImageResource(
                article.getBookmarked()
                        ? R.drawable.abc_btn_rating_star_on_mtrl_alpha
                        : R.drawable.abc_btn_rating_star_off_mtrl_alpha
        );
    }
}