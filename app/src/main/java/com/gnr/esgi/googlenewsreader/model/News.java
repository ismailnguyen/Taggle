package com.gnr.esgi.googlenewsreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ismail on 11-12-2015.
 */
public class News implements Parcelable{
    public int id;
    public String title;
    public String content;

    public News() {

    }

    protected News(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
    }
}
