package com.cookietech.adme.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cookietech.adme.R;

public class CustomToast {
    String text;

    public static Toast makeErrorToast(Context context, String message, int duration){
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast_error,((Activity) context).findViewById(R.id.custom_toast_container));
        TextView tv_message = layout.findViewById(R.id.tv_message);
        tv_message.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM,0,40);
        toast.setDuration(duration);
        toast.setView(layout);
        return toast;
    }

    public static Toast makeSuccessToast(Context context, String message, int duration){
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast_success,((Activity) context).findViewById(R.id.custom_toast_container));
        TextView tv_message = layout.findViewById(R.id.tv_message);
        tv_message.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM,0,40);
        toast.setDuration(duration);
        toast.setView(layout);
        return toast;
    }
}
