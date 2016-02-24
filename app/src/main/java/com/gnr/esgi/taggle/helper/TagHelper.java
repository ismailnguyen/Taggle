package com.gnr.esgi.taggle.helper;

import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.factory.TagFactory;
import com.gnr.esgi.taggle.model.Tag;
import java.util.List;

public class TagHelper {

    public static List<Tag> getTags() {
        return TagFactory.fromCursor(
                Taggle.getDao().getTags()
        );
    }
}
