package com.gnr.esgi.taggle.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.gnr.esgi.taggle.constant.DatabaseConstants;
import com.gnr.esgi.taggle.model.Article;
import com.gnr.esgi.taggle.model.Tag;

public class DatabaseLayer extends SQLiteOpenHelper {

    private final static String TAG = DatabaseLayer.class.getSimpleName();

    public DatabaseLayer(Context context) {
        super(
                context,
                DatabaseConstants.DB_NAME,
                null,
                DatabaseConstants.DB_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_ARTICLES =
                "CREATE TABLE " + DatabaseConstants.ArticleEntry.TABLE_NAME + " (" +
                        DatabaseConstants.ArticleEntry._ID + DatabaseConstants.INTEGER_TYPE + " PRIMARY KEY" + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_TITLE + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_DATE + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_CONTENT + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_SOURCE_NAME + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_SOURCE_URL + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_PICTURE_URL + DatabaseConstants.TEXT_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_READ + DatabaseConstants.INTEGER_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_DELETED + DatabaseConstants.INTEGER_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED + DatabaseConstants.INTEGER_TYPE + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME + DatabaseConstants.TEXT_TYPE + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_ARTICLES);

        String CREATE_TABLE_TAGS =
                "CREATE TABLE " + DatabaseConstants.TagEntry.TABLE_NAME + " (" +
                        DatabaseConstants.TagEntry._ID + DatabaseConstants.INTEGER_TYPE + " PRIMARY KEY" + DatabaseConstants.COMMA_SEPARATOR +
                        DatabaseConstants.TagEntry.COLUMN_NAME + DatabaseConstants.TEXT_TYPE + ")";

        sqLiteDatabase.execSQL(CREATE_TABLE_TAGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String DROP_TABLE_ARTICLES = "DROP TABLE IF EXISTS " + DatabaseConstants.ArticleEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_TABLE_ARTICLES);

        String DROP_TABLE_TAGS = "DROP TABLE IF EXISTS " + DatabaseConstants.TagEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_TABLE_TAGS);

        onCreate(sqLiteDatabase);
    }

    //region Articles

    public long addArticle(Article article) {
        // Check first if already article with same title fromCursor same source exists
        // in database, else insert
        return getArticles(
                article.getTitle(),
                article.getSource().getName()
            ).getCount() == 0
                ? getWritableDatabase()
                    .insert(
                            DatabaseConstants.ArticleEntry.TABLE_NAME,
                            null,
                            getContentValues(article)
                    )
                : 0;
    }

    public Cursor getArticles() {
        return getArticles(
                null,
                DatabaseConstants.ArticleEntry.COLUMN_DELETED + " = 0 ",
                null,
                null,
                null,
                null
        );
    }

    public Cursor getArticle(long rowID) {
        return getArticles(
                null,
                DatabaseConstants.ArticleEntry._ID + " = ? ",
                new String[]{String.valueOf(rowID)},
                null,
                null,
                null
        );
    }

    public Cursor getArticles(String title, String sourceName) {
        return getArticles(
                null,
                DatabaseConstants.ArticleEntry.COLUMN_TITLE + " LIKE ? "
                        + " AND " + DatabaseConstants.ArticleEntry.COLUMN_SOURCE_NAME + " LIKE ? ",
                new String[]{title, sourceName},
                null,
                null,
                null
        );
    }

    public Cursor getArticles(String tagName) {
        // Get only articles not manually deleted by user
        return getArticles(
                null,
                DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME + " LIKE ? "
                        + " AND " + DatabaseConstants.ArticleEntry.COLUMN_DELETED + " = 0 ",
                new String[]{tagName},
                null,
                null,
                null
        );
    }

    public Cursor getBookmarkedArticles() {
        // Get only articles not manually deleted by user
        return getArticles(
                null,
                DatabaseConstants.ArticleEntry.COLUMN_DELETED + " = 0 "
                        + " AND " + DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED + " = 1 ",
                null,
                null,
                null,
                null
        );
    }

    public Cursor getArticles(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sort) {
        return getReadableDatabase()
                .query(
                        DatabaseConstants.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sort
                );
    }

    public int updateArticle(Article article) {
        return getWritableDatabase()
                .update(
                        DatabaseConstants.ArticleEntry.TABLE_NAME,
                        getContentValues(article),
                        DatabaseConstants.ArticleEntry._ID + " = ?",
                        new String[]{String.valueOf(article.getArticleId())}
                );
    }

    public int bookmarkArticle(Article article) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED, article.getBookmarked() ? 1 : 0);

        return getWritableDatabase()
                .update(
                        DatabaseConstants.ArticleEntry.TABLE_NAME,
                        values,
                        DatabaseConstants.ArticleEntry._ID + " = ?",
                        new String[] { String.valueOf(article.getArticleId()) }
                );
    }

    public int deleteArticles() {
        // Never delete articles which were manually deleted by users
        // And bookmarked articles
        // Will be useful to retrieve only news not already deleted
        return deleteArticles(
                DatabaseConstants.ArticleEntry.COLUMN_DELETED + " = 0 "
                    + " AND " + DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED + " = 0 ",
                null
        );
    }

    public int deleteArticles(String[] rowIDs) {
        return deleteArticles(
                DatabaseConstants.ArticleEntry._ID + " IN (" + makePlaceholders(rowIDs.length) + ")",
                rowIDs
        );
    }

    public int deleteArticles(Tag tag) {
        return deleteArticles(
                DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME + " LIKE ? ",
                new String[] { tag.getName() }
        );
    }

    public int deleteArticles(String selection, String[] selectionArgs) {
        return getWritableDatabase()
                .delete(
                        DatabaseConstants.ArticleEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
    }

    //endregion

    // Erase all articles and tags
    public void purge() {
        deleteArticles(
                null,
                null
        );

        deleteTags(
                null,
                null
        );
    }

    //region Tags

    public long addTag(Tag tag) {
        // Insert tag if only tag with same name does not already exist in database
        return getTag(tag.getName()).getCount() == 0
                ? getWritableDatabase()
                .insert(
                        DatabaseConstants.TagEntry.TABLE_NAME,
                        null,
                        getContentValues(tag)
                )
                : 0;
    }

    public Cursor getTags() {
        return getTags(
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getTag(long rowID) {
        return getTags(
                null,
                DatabaseConstants.TagEntry._ID + " = ? ",
                new String[] { String.valueOf(rowID) },
                null,
                null,
                null
        );
    }

    public Cursor getTag(String name) {
        return getTags(
                null,
                DatabaseConstants.TagEntry.COLUMN_NAME + " LIKE ? ",
                new String[] { name },
                null,
                null,
                null
        );
    }

    public Cursor getTags(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sort) {
        return getReadableDatabase()
                .query(
                        DatabaseConstants.TagEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sort
                );
    }

    public int deleteTag(String name) {
        return deleteTags(
                DatabaseConstants.TagEntry.COLUMN_NAME + " LIKE ? ",
                new String[] { name }
        );
    }

    public int deleteTags() {
        return deleteTags(
                null,
                null
        );
    }

    public int deleteTags(String[] rowIDs) {
        return deleteTags(
                DatabaseConstants.TagEntry._ID + " IN (" + makePlaceholders(rowIDs.length) + ")",
                rowIDs
        );
    }

    public int deleteTags(String selection, String[] selectionArgs) {
        return getWritableDatabase()
                .delete(
                        DatabaseConstants.TagEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
    }

    //endregion

    private ContentValues getContentValues(Article article) {
        ContentValues values = new ContentValues();

        values.put(DatabaseConstants.ArticleEntry.COLUMN_TITLE, article.getTitle());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_DATE, article.getCreatedAt());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_CONTENT, article.getContent());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_SOURCE_NAME, article.getSource().getName());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_SOURCE_URL, article.getSource().getUrl());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_PICTURE_URL, article.getPicture().getPictureUrl());
        values.put(DatabaseConstants.ArticleEntry.COLUMN_READ, article.getRead() ? 1 : 0);
        values.put(DatabaseConstants.ArticleEntry.COLUMN_DELETED, article.getDeleted() ? 1 : 0);
        values.put(DatabaseConstants.ArticleEntry.COLUMN_BOOKMARKED, article.getBookmarked() ? 1 : 0);
        values.put(DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME, article.getLinkTagName());

        Log.d("DB COLUMN_TITLE", values.get(DatabaseConstants.ArticleEntry.COLUMN_TITLE).toString());
        Log.d("DB COLUMN_DATE", values.get(DatabaseConstants.ArticleEntry.COLUMN_DATE).toString());
        Log.d("DB COLUMN_CONTENT", values.get(DatabaseConstants.ArticleEntry.COLUMN_CONTENT).toString());
        Log.d("DB COLUMN_SOURCE_NAME", values.get(DatabaseConstants.ArticleEntry.COLUMN_SOURCE_NAME).toString());
        Log.d("DB COLUMN_SOURCE_URL", values.get(DatabaseConstants.ArticleEntry.COLUMN_SOURCE_URL).toString());
        Log.d("DB COLUMN_TAG_NAME", values.get(DatabaseConstants.ArticleEntry.COLUMN_TAG_NAME).toString());

        return values;
    }

    private ContentValues getContentValues(Tag tag) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.TagEntry.COLUMN_NAME, tag.getName());

        Log.d("DB COLUMN_NAME", values.get(DatabaseConstants.TagEntry.COLUMN_NAME).toString());

        return values;
    }

    private String makePlaceholders(int len) {
        if (len > 0) {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");

            for (int i = 1; i < len; i++)
                sb.append(", ?");

            return sb.toString();
        }

        throw new RuntimeException("No placeholders");
    }
}
