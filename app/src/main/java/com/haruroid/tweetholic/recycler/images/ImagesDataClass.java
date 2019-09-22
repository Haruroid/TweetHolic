package com.haruroid.tweetholic.recycler.images;

import android.graphics.Bitmap;

import android.net.Uri;

public class ImagesDataClass {
    private Bitmap thumb;
    private Uri imageuri;

    public void setImageuri(Uri imageuri) {
        this.imageuri = imageuri;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public Uri getImageuri() {
        return imageuri;
    }

    public Bitmap getThumb() {
        return thumb;
    }
}
