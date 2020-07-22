package com.cookietech.adme.Helpers;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class UiHelper {
    private Context context;
    private DisplayMetrics displayMetrics;

    public UiHelper(Context context) {
        displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public int getPercentOfDisplayWidthInPixels(float percent){
        return (int) (displayMetrics.widthPixels * percent);
    }

    public int getDisplayWidthInPixels(){
        return displayMetrics.widthPixels;
    }

    public int getDisplayHeightInPixels(){
        return displayMetrics.heightPixels;
    }

    public float getDisplayWidth(){
        return convertPixelsToDp(displayMetrics.widthPixels);
    }

    public float getDisplayHeight(){
        return convertPixelsToDp(displayMetrics.heightPixels);
    }

    public int getDisplayDensityInDpi(){
        return displayMetrics.densityDpi;
    }

    public void setViewWidth(View view,int widthInPixels){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = widthInPixels;
        view.setLayoutParams(layoutParams);
    }

    public void setViewHeight(View view,int widthInPixels){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = widthInPixels;
        view.setLayoutParams(layoutParams);
    }

    public int getViewWidth(View view){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return layoutParams.width;
    }

    public int getViewHeight(View view){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return layoutParams.height;
    }

    public float convertDpToPixels(int dp){
        return (dp * getDisplayDensityInDpi()) / 160F;
    }

    public float convertPixelsToDp(float px){
        return px / getDisplayDensityInDpi();
    }

    public void setMargins (View v, int l, int t, int r, int b) {
        l = (int) convertDpToPixels(l);
        t = (int) convertDpToPixels(t);
        r = (int) convertDpToPixels(r);
        b = (int) convertDpToPixels(b);
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
