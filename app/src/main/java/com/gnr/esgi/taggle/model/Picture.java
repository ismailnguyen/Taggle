package com.gnr.esgi.taggle.model;

import android.graphics.Bitmap;

public class Picture {

    private String pictureUrl;
    private Bitmap pictureBitmap;

    public Picture() {
    }

    public Picture(String url) {
        pictureUrl = url;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String url) {
        pictureUrl = url;
    }

    public Bitmap getPictureBitmap() {
        return pictureBitmap;
    }

    public void setPictureBitmap(Bitmap bitmap) {
        pictureBitmap = bitmap;
    }
}
