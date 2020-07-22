package com.cookietech.adme.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class MyReader {

    public static Bitmap readImageFromAssets(Context context,String imageName){
        try {
            InputStream is = context.getAssets().open(imageName);
            Bitmap bitmapImage = BitmapFactory.decodeStream(is);
            return bitmapImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
