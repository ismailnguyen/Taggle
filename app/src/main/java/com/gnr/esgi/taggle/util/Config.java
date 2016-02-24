package com.gnr.esgi.taggle.util;

import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;

public class Config {

    public static final boolean DISPLAY_LOG = true;
    public static final String PREFS_NAME = "GNRPrefs";
    public static final int AUTO_REFRESH_DELAY = 3600;

    // Version of Google News API
    public static final String API_VERSION = "1.0";

    // Edition
    public static final String API_EDITION = "fr";

    // Order by date
    public static final String API_ORDER = "d";

    // Show 8 results per page
    public static final String API_RESULTS = "8";

    // Headlines topic
    public static final String API_TOPIC = "h";

    public static Integer getTheme() {
        return Taggle.getSession().getNightMode()
                ? R.style.AppTheme_Dark
                : R.style.AppTheme_Light;
    }
}
