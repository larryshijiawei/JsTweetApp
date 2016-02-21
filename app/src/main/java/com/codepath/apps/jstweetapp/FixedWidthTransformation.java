package com.codepath.apps.jstweetapp;

import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Transformation;

/**
 * Created by jiaweishi on 2/19/16.
 */
public class FixedWidthTransformation implements Transformation {
    private int width;

    public FixedWidthTransformation(int fixedWidth){
        width = fixedWidth;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        //Log.d("TAG", "original size is "+ source.getHeight() + " " + source.getWidth());
        int newHeight = (source.getHeight() * width) / source.getWidth();
        Bitmap result = Bitmap.createScaledBitmap(source, width, newHeight, true);
        if(result != source)
            source.recycle();

        return result;
    }

    @Override
    public String key() { return "square()"; }
}
