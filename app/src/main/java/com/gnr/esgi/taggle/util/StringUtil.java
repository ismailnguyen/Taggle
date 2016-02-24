package com.gnr.esgi.taggle.util;

public class StringUtil {

    public static String capitalize(String source) {
        return Character.toUpperCase(source.charAt(0))
                + source.substring(1);
    }
}
