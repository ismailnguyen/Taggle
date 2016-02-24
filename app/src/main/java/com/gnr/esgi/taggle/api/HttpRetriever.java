package com.gnr.esgi.taggle.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;
import com.gnr.esgi.taggle.util.Config;

public class HttpRetriever
{
    public static String retrieveStream(String str) {
        try{
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            switch (connection.getResponseCode()) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while((line = br.readLine()) != null)
                        sb.append(line);

                    br.close();
                    return sb.toString();
            }
        } catch (IOException e) {
            if(Config.DISPLAY_LOG)
                Log.w("HttpRetriever", "Error for URL " + str, e);
        }

        return null;
    }
}
