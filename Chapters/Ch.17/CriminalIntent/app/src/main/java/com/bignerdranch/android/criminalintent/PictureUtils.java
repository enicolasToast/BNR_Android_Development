package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how much to scale down by
        int sampleSize = 1;
        if(srcWidth > destWidth || srcHeight > destHeight) {
            if(srcWidth > srcHeight) {
                sampleSize = Math.round(srcWidth / destWidth);
            } else {
                sampleSize = Math.round(srcHeight / destHeight);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
