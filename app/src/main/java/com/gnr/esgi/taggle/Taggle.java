package com.gnr.esgi.taggle;

import android.app.Application;
import android.content.Context;
import com.gnr.esgi.taggle.dataaccess.DatabaseLayer;
import com.gnr.esgi.taggle.model.SessionManager;

public class Taggle extends Application {

    private static Context context;
    private static DatabaseLayer dao;
    private static SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }

    public static SessionManager getSession() {
        if(session == null)
            session = new SessionManager();

        return session;
    }

    public static DatabaseLayer getDao() {
        if(dao == null)
            dao = new DatabaseLayer(getAppContext());

        return dao;
    }
}
