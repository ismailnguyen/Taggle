package com.gnr.esgi.taggle.factory;

import android.database.Cursor;
import android.util.Log;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.parser.ArticleParser;
import com.gnr.esgi.taggle.util.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ArticleFactory {

    public static List<Article> fromJson(String response) {
        ArrayList<Article> articlesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("responseData")) {
                JSONObject responseData = jsonObject.getJSONObject("responseData");

                if(responseData.has("results")) {
                    JSONArray results = responseData.getJSONArray("results");

                    for (int i=0; i < results.length(); i++)
                        articlesList.add(
                                ArticleParser.parse(
                                        new Article(
                                                results.getJSONObject(i)
                                        )
                                )
                        );
                }
            }
        } catch (JSONException e) {
            if(Config.DISPLAY_LOG)
                Log.w("ArticleFactory", e);
        }

        return articlesList;
    }

    public static Article fromCursor(Cursor cursor) {
        cursor.moveToFirst();

        return new Article(cursor);
    }

    public static List<Article> fromCursorToList(Cursor cursor) {
        ArrayList<Article> articlesList = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            articlesList.add(
                    new Article(
                            cursor
                    )
            );

        return articlesList;
    }
}