package com.gnr.esgi.taggle.activitie;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.adapter.ListArticlesAdapter;
import com.gnr.esgi.taggle.constant.ArticleConstants;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.listener.ArticlesMultiChoiceModeListener;
import com.gnr.esgi.taggle.listener.CancelTaskOnListener;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.service.RefreshService;
import com.gnr.esgi.taggle.task.DatabaseTask;
import com.gnr.esgi.taggle.util.Config;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    boolean isRefresh = Taggle.getSession().getAutoUpdate();
    Integer currentPage = 0;
    RelativeLayout relativeLayout;
    public static List<Article> articlesList = new ArrayList<>();
    ListView articlesListView;
    ListArticlesAdapter articlesAdapter;
    static LocalBroadcastManager broadcaster;
    ProgressDialog progressDialog;
    AppBarLayout appBar;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    public static Button loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Config.getTheme());
        setContentView(R.layout.activity_home);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.button_refresh);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySnackbar(
                        refreshListArticles()
                );
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTags();
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        articlesListView = (ListView) findViewById(R.id.news_list);

        loadMore = new Button(this);
        loadMore.setText(getString(R.string.articles_load_more));
        loadMore.setVisibility(View.GONE);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreArticles();
            }
        });

        articlesListView.addFooterView(loadMore);

        articlesAdapter = new ListArticlesAdapter(getApplicationContext(), articlesList);
        articlesListView.setAdapter(articlesAdapter);
        articlesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        articlesListView.setMultiChoiceModeListener(
                new ArticlesMultiChoiceModeListener(
                        articlesListView,
                        articlesAdapter,
                        toolbar,
                        appBar,
                        floatingActionButton
                )
        );

        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Update database, set read true
                ArticleHelper.setRead(articlesList.get(position));

                articlesAdapter.notifyDataSetChanged();

                openArticleDetail(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        initServices();

        // At application start, if database contains no articles, load fromCursor internet
        if(ArticleHelper.getArticles().isEmpty()) {
            displaySnackbar(
                    refreshListArticles()
            );
        }

        refreshListView();

        // Set title of activity with current tag name
        setTitle(Taggle.getSession().getCurrentTag().getName());
    }

    @Override
    public void onResume() {
        super.onResume();

        articlesAdapter.swapItems(articlesList);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //broadcaster.unregisterReceiver(broadcastReceiverFromHomeActivity);
    }

    // Erase entire articles list and update it with fresh news
    public Integer refreshListArticles() {
        // Save old news to compare with latest
        List<Article> oldArticles = articlesList;

        // First refresh articles in database getting new content fromCursor internet
        ArticleHelper.refreshArticles();

        // Then refresh view
        refreshListView();

        return ArticleHelper.countRecentNews(
                articlesList,
                oldArticles
        );
    }

    // Keep current articles, load new articles from internet and add them to previous articles list
    public Integer loadMoreArticles() {
        // Save old news to compare with latest
        List<Article> oldArticles = articlesList;

        // Update number of current page, then get news from that page without deleting previous news
        ArticleHelper.moreArticles(++currentPage);

        // Then refresh view
        refreshListView();

        return ArticleHelper.countRecentNews(
                articlesList,
                oldArticles
        );
    }

    private void refreshListView() {
        //Clean listArticles list before refresh
        articlesList.clear();

        DatabaseTask task = new DatabaseTask(articlesAdapter, progressDialog);
        //progressOperation(task);
        task.execute();
    }

    private void initServices() {
        if(Taggle.getSession().getAutoUpdate()) {
            if (!isRefreshServiceRunning(RefreshService.class)) {
                broadcaster = LocalBroadcastManager.getInstance(this);

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(RefreshService.NEW_ARTICLES_ARE_READY);

                broadcaster.registerReceiver(
                        broadcastReceiverFromHomeActivity,
                        intentFilter
                );

                launchRefreshService();
            }
        }
        else {
            if (isRefreshServiceRunning(RefreshService.class)) {
                stopRefreshService();
            }
        }
    }

    private void openArticleDetail(int position) {
        Intent intent = new Intent(this, DetailArticleActivity.class);

        intent.putExtra(
                ArticleConstants.ARTICLE_KEY_ID,
                articlesList.get(position).getArticleId()
        );

        startActivity(intent);
    }

    private final BroadcastReceiver broadcastReceiverFromHomeActivity = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String message = intent.getAction();

            if(message.equals(RefreshService.NEW_ARTICLES_ARE_READY)) {
                if(Config.DISPLAY_LOG)
                    Log.d(
                            RefreshService.TAG,
                            "receiveBroadcastMessageFromHomeActivity " + RefreshService.NEW_ARTICLES_ARE_READY
                    );

                refreshListView();
            }
        }
    };

    private void displaySnackbar(Integer count) {
        String message =
                new StringBuilder()
                    .append(count)
                    .append(" ")
                    .append(getString(R.string.snackbar_addedNews))
                    .toString();

        final Snackbar snackbar = Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    public void showTagSettings() {
        startActivity(
                new Intent(
                        this,
                        TagSettingsActivity.class
                )
        );
    }

    public void showTags() {
        startActivity(
                new Intent(
                        this,
                        TagActivity.class
                )
        );
    }

    public void showSettings() {
        startActivity(
                new Intent(
                        this,
                        SettingsActivity.class
                )
        );
    }

    public void showBookmarks() {
        startActivity(
                new Intent(
                        this,
                        BookmarksActivity.class
                )
        );
    }

    public void launchRefreshService() {
        if(Config.DISPLAY_LOG)
            Log.d(
                    RefreshService.TAG,
                    "Launch refresh service"
            );

       // If refresh service isn't already running, start it
        if (!isRefreshServiceRunning(RefreshService.class))
            startService(
                    new Intent(
                            this,
                            RefreshService.class
                    )
            );
    }

    public void stopRefreshService() {
        if (Config.DISPLAY_LOG)
            Log.d(
                    RefreshService.TAG,
                    "Stop refresh service"
            );

        // If refresh service is already running, stop it
        if (isRefreshServiceRunning(RefreshService.class))
            stopService(
                    new Intent(
                            this,
                            RefreshService.class
                    )
            );
    }

    public boolean isRefreshServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    private void progressOperation(AsyncTask<?, ?, ?> task) {
        progressDialog = ProgressDialog.show(
                HomeActivity.this,
                getString(R.string.progressDialog_title),
                getString(R.string.progressDialog_message),
                true,
                true
        );

        progressDialog.setOnCancelListener(
                new CancelTaskOnListener(
                        task,
                        articlesAdapter
                )
        );
    }

    public void showRefreshDialog() {
        final CharSequence[] items = { getString(R.string.refreshDialog_message) };
        boolean[] checkedItems = new boolean[] { isRefresh };

        final AlertDialog.Builder builder
            = new AlertDialog
                    .Builder(this)
                    .setTitle(R.string.refreshDialog_title)
                    .setCancelable(false)
                    .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            isRefresh = isChecked;
                        }
                    })
                    .setPositiveButton(R.string.refreshDialog_accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Taggle.getSession().setAutoUpdate(isRefresh);

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
        getMenuInflater().inflate(
                R.menu.menu_home,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                showRefreshDialog();
                return true;

            /*case R.id.action_tags:
                showTags();
                return true;*/

            case R.id.action_search:
                showTagSettings();
                return true;

            case R.id.action_settings:
                showSettings();
                return true;

            case R.id.action_bookmarks:
                showBookmarks();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
