package com.gnr.esgi.taggle.parser;

import android.text.Html;

public final class HtmlParser {

    public static final String ELLIPSIS = "&ellip;";
    public static final String TAG_PATTERN = "<[^>]+>";
    public static final String WHITESPACE_PATTERN = "\\s!+";

    public static String removeTags(String source) {
        return source
                .replaceAll(TAG_PATTERN, "")
                .replaceAll(WHITESPACE_PATTERN, " ")
                .trim();
    }

    public static String escapeSpace(String source) {
        return source
                .replaceAll(" ", "+"); //Or by %20 for space in URL
    }

    public static String parse(String source) {
        final int bodyIndex = source.indexOf("<body");

        if(bodyIndex > -1)
            source = source.substring(bodyIndex);

        return removeTags(Html.fromHtml(source).toString());
    }
}
