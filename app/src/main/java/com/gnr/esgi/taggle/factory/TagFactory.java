package com.gnr.esgi.taggle.factory;

import android.database.Cursor;
import com.gnr.esgi.taggle.model.Tag;
import java.util.ArrayList;
import java.util.List;

public class TagFactory {

    public static List<Tag> fromCursor(Cursor cursor) {
        ArrayList<Tag> tagsList = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            tagsList.add(new Tag(cursor));

        return tagsList;
    }
}
