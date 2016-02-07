package com.gnr.esgi.googlenewsreader.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.gnr.esgi.googlenewsreader.GNRApplication;
import com.gnr.esgi.googlenewsreader.R;
import com.gnr.esgi.googlenewsreader.adapters.ListArticlesAdapter;
import com.gnr.esgi.googlenewsreader.constants.ArticleConstants;
import com.gnr.esgi.googlenewsreader.helper.ArticleHelper;
import com.gnr.esgi.googlenewsreader.models.Article;
import com.gnr.esgi.googlenewsreader.models.Tag;
import com.gnr.esgi.googlenewsreader.services.RefreshService;
import com.gnr.esgi.googlenewsreader.utils.Config;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity {

    public static final String ACTIVATE_AUTO_REFRESH = "activate_auto_refresh";
    public static final String DESACTIVATE_AUTO_REFRESH = "desactivate_auto_refresh";
    public static final String DISPLAY_NEW_ARTICLES = "display_new_articles";
    boolean isRefresh = false;

    RelativeLayout relativeLayout;
    List<Article> articlesArrayList = new ArrayList<>();
    ListView listviewArticles;
    ListArticlesAdapter listArticlesAdapter;
    static LocalBroadcastManager broadcaster;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySnackbar(refreshListArticles());
            }
        });

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        listviewArticles = (ListView) findViewById(R.id.news_list);

        listArticlesAdapter = new ListArticlesAdapter(getApplicationContext(), articlesArrayList);
        listviewArticles.setAdapter(listArticlesAdapter);

        listviewArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                articlesArrayList.get(position).setHasAlreadyReadValue(true);
                sendDataToDetailArticleActivity(position);
            }
        });
    }

    public Integer refreshListArticles() {
        // Save old news to compare with latest
        List<Article> oldArticles = articlesArrayList;

        // First refresh articles in database getting new content from internet
        ArticleHelper.refreshArticles();

        // Then refresh view
        refreshListView();

        return ArticleHelper.countRecentNews(articlesArrayList, oldArticles);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GNRApplication.getUser().setSettings(getSharedPreferences(Config.PREFS_NAME, 0));

        initServices();

        if(GNRApplication.getUser().getData().getAllArticles().isEmpty())
            ArticleHelper.refreshArticles();
        
        refreshListView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshListView() {
        //Clean articles list before refresh
        articlesArrayList.clear();

        for (Tag tag : GNRApplication.getUser().getData().getTags())
            articlesArrayList.addAll(tag.getArticlesList());

        ArticleHelper.sortByDate(articlesArrayList);

        listArticlesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverFromHomeActivity);
    }

    private void initServices() {
        if(GNRApplication.getUser().getAutoUpdate()) {
            broadcaster = LocalBroadcastManager.getInstance(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(RefreshService.NEW_ARTICLES_ARE_READY);
            //intentFilter.addAction(DISPLAY_NEW_ARTICLES);
            //intentFilter.addAction(ACTIVATE_AUTO_REFRESH);
            //intentFilter.addAction(DESACTIVATE_AUTO_REFRESH);
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverFromHomeActivity, intentFilter);

            launchRefreshService();
        }
        else {
            stopRefreshService();
        }
    }

    private void sendDataToDetailArticleActivity(int position) {
        Intent intent = new Intent(this, DetailArticleActivity.class);
        intent.putExtra(ArticleConstants.ARTICLE_KEY_TITLE, articlesArrayList.get(position).getTitle());
        intent.putExtra(ArticleConstants.ARTICLE_KEY_CONTENT, articlesArrayList.get(position).getContent());
        intent.putExtra(ArticleConstants.ARTICLE_KEY_CREATED_AT, articlesArrayList.get(position).getCreatedAt());
        intent.putExtra(ArticleConstants.ARTICLE_KEY_SOURCE_NAME, articlesArrayList.get(position).getSource().getSourceName());
        intent.putExtra(ArticleConstants.ARTICLE_KEY_SOURCE_URL, articlesArrayList.get(position).getSource().getSourceUrl());
        intent.putExtra(ArticleConstants.ARTICLE_KEY_PICTURE_URL, articlesArrayList.get(position).getPictureUrl());

        startActivity(intent);
    }

    private final BroadcastReceiver broadcastReceiverFromHomeActivity = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String message = intent.getAction();
            if (message.equals(RefreshService.NEW_ARTICLES_ARE_READY)) {
                Log.d(Config.LOG_PREFIX, "receiveBroadcastMessageFromHomeActivity " + RefreshService.NEW_ARTICLES_ARE_READY);
                refreshListView();
            }
            if (message.equals(DISPLAY_NEW_ARTICLES)) {
                Log.d(Config.LOG_PREFIX, "broadcastReceiverFromHomeActivity " + DISPLAY_NEW_ARTICLES);
                displaySnackbar(refreshListArticles());
            }
            if (message.equals(ACTIVATE_AUTO_REFRESH)) {
                Log.d(Config.LOG_PREFIX, "broadcastReceiverFromHomeActivity "+ACTIVATE_AUTO_REFRESH);
            }
            if (message.equals(DESACTIVATE_AUTO_REFRESH)) {
                Log.d(Config.LOG_PREFIX, "broadcastReceiverFromHomeActivity "+DESACTIVATE_AUTO_REFRESH);
            }
        }
    };

    private void displaySnackbar(Integer count) {
        StringBuilder message = new StringBuilder();
        message.append(count);
        message.append(R.string.snackbar_addedNews);

        final Snackbar snackbar = Snackbar.make(relativeLayout, message.toString(), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastMessageFromHomeActivity(DISPLAY_NEW_ARTICLES);
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    public void showTagSettings() {
        Intent intent = new Intent(this, TagSettingsActivity.class);
        startActivity(intent);
    }

    public void showTags() {
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }

    private void broadcastMessageFromHomeActivity(final String message) {
        if(Config.DISPLAY_LOG)
            Log.d(Config.LOG_PREFIX, "broadcastMessageFromHomeActivity " + message);

        final Intent intent = new Intent(message);
        broadcaster.sendBroadcast(intent);
    }

    public void launchRefreshService() {
        if(Config.DISPLAY_LOG)
            Log.d(Config.LOG_PREFIX, "launchRefreshService startService");

        startService(new Intent(this, RefreshService.class));
    }

    public void stopRefreshService() {
        if(Config.DISPLAY_LOG)
            Log.d(Config.LOG_PREFIX, "launchRefreshService stopRefreshService");

        stopService(new Intent(this, RefreshService.class));
    }

    public void showRefreshDialog() {
        final CharSequence[] items = { "Refresh every hour" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.refreshDialog_title)
                .setCancelable(false)
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        isRefresh = isChecked;
                    }
                })
                .setPositiveButton(R.string.refreshDialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GNRApplication.getUser().setAutoUpdate(isRefresh);

                        initServices();

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.refreshDialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                showRefreshDialog();
                return true;

            case R.id.action_tags:
                showTags();
                return true;

            case R.id.action_search:
                showTagSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
