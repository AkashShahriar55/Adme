package com.example.adme.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CookieTechUtilityClass {

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String date = month+" "+year;
        return date;
    }

    public static String getTimeDate(String timeInMillis, String format) {
        long time = Long.parseLong(timeInMillis);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        SimpleDateFormat sd = new SimpleDateFormat(format, Locale.getDefault());
        return sd.format(cal.getTime());
    }

    public static String getTimeDifference(String startTimeInMillis, String endTimeInMillis) {
        long startTime = Long.parseLong(startTimeInMillis);
        Date startDate = new Date(startTime);
        long endTime = Long.parseLong(endTimeInMillis);
        Date endDate = new Date(endTime);
        long different = endDate.getTime() - startDate.getTime();
        Log.d("CookieTechUtilityClass", startDate+" getTimeDifference: "+endDate);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedMonths > 0){
            return (elapsedMonths + " month ago");
        }else if(elapsedDays > 0){
            return (elapsedDays + " day ago");
        }else if(elapsedHours > 0){
            return (elapsedHours + " hour ago");
        }else if(elapsedMinutes > 0){
            return (elapsedMinutes + " min ago");
        } else {
            return (elapsedSeconds + " second ago");
        }
    }

    public static void setSharedPreferences(String key, String value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedPreferences(String key, Context context){
        SharedPreferences preferences = context.getSharedPreferences("Settings", MODE_PRIVATE);
        return preferences.getString(key,"");
    }
}
