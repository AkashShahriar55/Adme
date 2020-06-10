package com.example.adme.Helpers;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class CookieTechUtilityClass {

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String date = month+" "+year;
        return date;
    }
}
