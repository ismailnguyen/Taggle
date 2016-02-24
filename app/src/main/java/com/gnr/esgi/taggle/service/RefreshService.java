package com.gnr.esgi.taggle.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.helper.ArticleHelper;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.util.Config;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RefreshService extends Service {

    public static final String TAG = "RefreshService";
    public static final String NEW_ARTICLES_ARE_READY = "NEW_ARTICLES_ARE_READY";
    static LocalBroadcastManager broadcaster;
    static boolean firstTime = true;

    @Override
    public void onCreate(){
        super.onCreate();

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

                try {
                    if(Config.DISPLAY_LOG)
                        Log.d(TAG, "Refresh service is running");

                    // Don't execute refreshing at launch of application
                    if (!firstTime) {
                        List<Article> oldArticles = ArticleHelper.getArticles();

                        // Refresh articles list
                        ArticleHelper.refreshArticles();

                        int count = ArticleHelper.countRecentNews(
                                ArticleHelper.getArticles(),
                                oldArticles
                        );

                        // If news were added
                        if (count > 0) {
                            // Send broadcast to inform news were added
                            sendBroadcastMessageFromRefreshService(NEW_ARTICLES_ARE_READY);

                            // Show notification to user to inform how many news were added
                            sendNotification(count);
                        }
                    }
                    else {
                        firstTime = false;
                    }

                } catch (Exception e) {
                    if(Config.DISPLAY_LOG)
                        Log.d(TAG, e.getMessage());
                }
            }
        }, 0, Config.AUTO_REFRESH_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendBroadcastMessageFromRefreshService(final String message) {
        broadcaster.sendBroadcast(
                new Intent(
                        message
                )
        );
    }

    public void sendNotification(int count) {
        String title =
            new StringBuilder()
                .append(count)
                .append(" ")
                .append(getString(R.string.notification_title))
                .toString();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(getString(R.string.notification_content));

        Intent targetIntent = new Intent(this, RefreshService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(12345, builder.build());
    }
}
