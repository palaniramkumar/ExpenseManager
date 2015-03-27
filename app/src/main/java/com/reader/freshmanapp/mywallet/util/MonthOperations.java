package com.reader.freshmanapp.mywallet.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Ramkumar on 11/02/15.
 */
public class MonthOperations {
    public static final String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEPT", "OCT", "NOV", "DEC"};

    public static int getMonthAsInt(String name) {
        return Collections.indexOfSubList(Arrays.asList(months), Arrays.asList(name));
    }

    public static String getMonthAsString(int index) {
        return months[index];
    }

    public static Date next(String name, String y) {

        Date date = new Date();
        date.setMonth(getMonthAsInt(name));
        date.setYear(Integer.parseInt(y));
        date.setDate(1);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 1);

        return c.getTime();
    }

    public static Date previous(String name, String y) {
        Date date = new Date();
        date.setMonth(getMonthAsInt(name));
        date.setYear(Integer.parseInt(y));
        date.setDate(1);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);

        return c.getTime();
    }

    public static String getMonthin2Digit(int month) {
        if (month < 10)
            return "0" + month;
        else
            return month + "";
    }

}

