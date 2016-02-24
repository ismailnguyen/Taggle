package com.gnr.esgi.taggle.util;

import android.util.Log;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static Date parse(String date) {
        Date parsedDate = new Date();

        try {
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM yyyy HH:mm:ss -0800", Locale.ENGLISH);

            parsedDate = dateFormat.parse(date);
        }
        catch (ParseException e){
            Log.d("DateUtil", e.getMessage());
        }

        return parsedDate;
    }

    public static String getDuration(String date) {
        Date parsedDate = parse(date);
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMMM d yyy");

        Long hoursDuration = TimeUnit.MILLISECONDS.toHours(now.getTime() - parsedDate.getTime());
        Long daysDuration = TimeUnit.MILLISECONDS.toDays(now.getTime() - parsedDate.getTime());

        return hoursDuration == 1
                ? Taggle.getAppContext().getString(R.string.hour_ago)
                : hoursDuration < 24
                    ? hoursDuration + " " + Taggle.getAppContext().getString(R.string.hours_ago)
                    : daysDuration == 1
                        ? Taggle.getAppContext().getString(R.string.day_ago)
                        : daysDuration < 31
                            ? daysDuration + " " + Taggle.getAppContext().getString(R.string.days_ago)
                            : dateFormat.format(date);
    }
}
