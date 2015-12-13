package com.gnr.esgi.googlenewsreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gnr.esgi.googlenewsreader.R;
import com.gnr.esgi.googlenewsreader.adapter.LazyAdapter;
import com.gnr.esgi.googlenewsreader.database.DatabaseManager;
import com.gnr.esgi.googlenewsreader.model.News;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends Activity {

    ListView listview;
    LazyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //openNews(3);
            }
        });

        DatabaseManager databaseManager = new DatabaseManager();

        listview = (ListView) findViewById(R.id.news_list);

        adapter = new LazyAdapter(this, databaseManager.getAllNews());
        listview.setAdapter(adapter);

        // Click event on news list item
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void openNews(int idNews) throws MalformedURLException {
        Intent intent = new Intent(this, NewsActivity.class);

        News news = new News();
        news.setId(idNews);
        news.setTitle("ESGI c�l�bre 40ans d'existence");
        news.setContent("Apr�s 40ans d'existence l'�cole Sup�rieure de G�nie Informatique plac�e � Nation c�l�bre son anniversaire.");

        intent.putExtra("news", news);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
