package com.gnr.esgi.taggle.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;

public class NetworkUtil {
    public static boolean checkInternetConnection(){
        Context context = Taggle.getAppContext();
        ConnectivityManager check = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (check != null)
        {
            NetworkInfo[] info = check.getAllNetworkInfo();

            if (info != null)
                for (int i = 0; i <info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }

        return false;
    }

    public static void showInvalidNetworkMessage() {
        Toast
            .makeText(
                    Taggle.getAppContext(),
                    R.string.no_internet,
                    Toast.LENGTH_LONG
            )
            .show();
    }
}
