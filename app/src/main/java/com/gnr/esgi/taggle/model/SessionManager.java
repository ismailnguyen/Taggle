package com.gnr.esgi.taggle.model;

import android.content.SharedPreferences;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.constant.TagConstants;
import com.gnr.esgi.taggle.util.Config;

public class SessionManager {

    private SharedPreferences settings;

    public SessionManager() {
        settings = Taggle
                    .getAppContext()
                    .getSharedPreferences(
                        Config.PREFS_NAME,
                        0
                    );
    }

    public Boolean getAutoUpdate() {
        return settings.getBoolean(
                "autoRefresh",
                false);
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("autoRefresh", autoUpdate);

        editor.commit();
    }

    public Tag getCurrentTag() {
        return new Tag(
                settings.getString(
                        "currentTag",
                        TagConstants.ALL
                )
        );
    }

    public void setCurrentTag(Tag currentTag) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(
                "currentTag",
                currentTag.getName()
        );

        editor.commit();
    }

    public Boolean getNightMode() {
        return settings.getBoolean(
                        "nightMode",
                        false
        );
    }

    public void setNightMode(Boolean nightMode) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(
                "nightMode",
                nightMode
        );

        editor.commit();
    }
}
