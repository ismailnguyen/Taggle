package com.gnr.esgi.taggle.constant;

import android.provider.BaseColumns;

public class DatabaseConstants {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "googlenewsreader.db";

    public final static String TEXT_TYPE = " TEXT";
    public final static String REAL_TYPE = " REAL";
    public final static String INTEGER_TYPE = " INTEGER";
    public final static String COMMA_SEPARATOR = ",";

    public abstract class ArticleEntry implements BaseColumns {

        public static final String TABLE_NAME = "articles";

        public static final String COLUMN_TITLE = "article_title";
        public static final String COLUMN_DATE = "article_date";
        public static final String COLUMN_CONTENT = "article_content";
        public static final String COLUMN_PICTURE_URL = "article_picture_url";
        public static final String COLUMN_SOURCE_NAME = "article_source_name";
        public static final String COLUMN_SOURCE_URL = "article_source_url";
        public static final String COLUMN_TAG_NAME = "article_tag_name";
        public static final String COLUMN_READ = "article_read";
        public static final String COLUMN_DELETED = "article_deleted";
        public static final String COLUMN_BOOKMARKED = "article_bookmarked";
    }

    public abstract class TagEntry implements BaseColumns {

        public static final String TABLE_NAME = "tags";

        public static final String COLUMN_NAME = "tag_name";
    }
}
