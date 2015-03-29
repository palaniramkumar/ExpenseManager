package com.reader.freshmanapp.mywallet.util;

import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Ramkumar on 11/02/15.
 */
public class MonthOperations {
    public static final String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    public static int getMonthAsInt(String name) {
        return Collections.indexOfSubList(Arrays.asList(months), Arrays.asList(name));
    }

    public static String getMonthAsString(int index) {
        return months[index%12];
    }

    public static Date next(String name, String y) {


        Calendar date = Calendar.getInstance();
        date.set(Integer.parseInt(y), getMonthAsInt(name), 1);
        date.add(Calendar.MONTH, 1);
        return date.getTime();
    }

    public static Date previous(String name, String y) {

        Calendar date = Calendar.getInstance();
        Log.e("Month Year ", Integer.parseInt(y)+"");
        Log.e("Month Parsed",getMonthAsInt(name)+"");
        date.set(Integer.parseInt(y), getMonthAsInt(name), 1);
        date.add(Calendar.MONTH, -1);
        Log.e("Month: Updated year", date.get(Calendar.YEAR)+"");
        Log.e("Month: Updated Month",  date.get(Calendar.MONTH)+"");
        return date.getTime();


        /*Date date = new Date();
        date.setMonth(getMonthAsInt(name));
        date.setYear(Integer.parseInt(y));
        date.setDate(1);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Log.e("Month Name", name);
        Log.e("Month Year ", y);
        Log.e("Month Parsed",getMonthAsInt(name)+"");

        c.add(Calendar.MONTH, 0);

        return c.getTime();*/
    }

    public static String getMonthin2Digit(int month) {
        if (month < 10)
            return "0" + month;
        else
            return month + "";
    }

}

