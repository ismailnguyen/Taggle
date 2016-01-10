package com.gnr.esgi.googlenewsreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.gnr.esgi.googlenewsreader.R;
import com.gnr.esgi.googlenewsreader.model.Article;

/**
 * Created by valerie on 06/01/16.
 */
public class GNRDatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = GNRDatabaseHelper.class.getSimpleName();
    
    public GNRDatabaseHelper(Context context) {
        super(context, context.getString(R.string.app_settings_db_name), null, Integer.valueOf("1"));
    }

    public final class ArticleContract {
        public ArticleContract() {}

        public abstract class ArticleEntry implements BaseColumns {
            public static final String TABLE_NAME = "articles";

            public static final String COLUMN_TITLE = "article_title";
            public static final String COLUMN_DATE = "article_date";
            public static final String COLUMN_CONTENT = "article_content";
            public static final String COLUMN_SOURCE_NAME = "article_source_name";
            public static final String COLUMN_SOURCE_URL = "article_source_url";
        }
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String TEXT_TYPE = " TEXT";
        String REAL_TYPE = " REAL";
        String INTEGER_TYPE = " NUMERIC";
        String COMMA_SEPARATOR = ",";

        String CREATE_TABLE_ARTICLES =
                "CREATE TABLE " + ArticleContract.ArticleEntry.TABLE_NAME + " (" +
                        ArticleContract.ArticleEntry._ID + INTEGER_TYPE + " PRIMARY_KEY" + COMMA_SEPARATOR +
                        ArticleContract.ArticleEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEPARATOR +
                        ArticleContract.ArticleEntry.COLUMN_DATE + TEXT_TYPE + COMMA_SEPARATOR +
                        ArticleContract.ArticleEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEPARATOR +
                        ArticleContract.ArticleEntry.COLUMN_SOURCE_NAME + TEXT_TYPE + COMMA_SEPARATOR +
                        ArticleContract.ArticleEntry.COLUMN_SOURCE_URL + TEXT_TYPE + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_ARTICLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String DROP_TABLE_ARTICLES = "DROP TABLE IF EXISTS " + ArticleContract.ArticleEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_TABLE_ARTICLES);
        onCreate(sqLiteDatabase);
    }

    ///////////////
    ////ARTICLES//////
    /////////////

    public long addArticle(Article article) {
        ContentValues values = new ContentValues();
        values.put(ArticleContract.ArticleEntry.COLUMN_TITLE, article.getTitle());
        values.put(ArticleContract.ArticleEntry.COLUMN_DATE, "date");
        values.put(ArticleContract.ArticleEntry.COLUMN_CONTENT, article.getContent());
        values.put(ArticleContract.ArticleEntry.COLUMN_SOURCE_NAME, article.getSourceName());
        values.put(ArticleContract.ArticleEntry.COLUMN_SOURCE_URL, article.getSourceUrl());

        long newRowId = this.getWritableDatabase().insert(
                ArticleContract.ArticleEntry.TABLE_NAME,
                null,
                values
        );

        return newRowId;
    }

    public Cursor getAllArticles() {
        return null;
    }

    public Cursor getArticles(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sort) {
        return this.getReadableDatabase().query(
                ArticleContract.ArticleEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sort);
    }

    public Cursor getArticleById(long rowID) {
        return getArticles(null, ArticleContract.ArticleEntry._ID + "=?", new String[]{String.valueOf(rowID)}, null, null, null);
    }

    public int deleteAllArticles(){
        return deleteArticles(null, null);
    }

    public int deleteArticles(String selection, String[] selectionArgs){
        return this.getWritableDatabase().delete(
                ArticleContract.ArticleEntry.TABLE_NAME,
                selection,
                selectionArgs);
    }

    public int deleteAdByIDs(String[] rowIDs){
        return deleteArticles(ArticleContract.ArticleEntry._ID + " IN (" + makePlaceholders(rowIDs.length) + ")", rowIDs);
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
