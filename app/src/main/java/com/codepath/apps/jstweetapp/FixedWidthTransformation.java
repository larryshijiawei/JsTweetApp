package com.codepath.apps.jstweetapp;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by jiaweishi on 2/19/16.
 */
public class FixedWidthTransformation implements Transformation {
    private final int WIDTH = 600;

    @Override
    public Bitmap transform(Bitmap source) {
        int newHeight = (source.getHeight() * WIDTH) / source.getWidth();
        Bitmap result = Bitmap.createScaledBitmap(source, WIDTH, newHeight, false);
        if(result != source)
            source.recycle();

        return result;
    }

    @Override
    public String key() { return "square()"; }
}
