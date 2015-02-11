package com.reader.ramkumar.expensemanager.util;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Ramkumar on 11/02/15.
 */
public class MonthOperations {
    public static  final String  [] months= {"JANUARY","FEB","MARCH","APRIL","MAY","JUN","JULY","AUG","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
    public static int getMonthAsInt(String name){
        return Collections.indexOfSubList(Arrays.asList(months), Arrays.asList(name));
    }
    public static String getMonthAsString(int index){
        return months[index];
    }
    public static String next(String name){
        return getMonthAsString(getMonthAsInt(name)+1);
    }
    public static String previous(String name){
        return getMonthAsString(getMonthAsInt(name)-1);
    }
    public static String getMonthin2Digit(int month){
        if(month < 10)
            return "0"+month;
        else
            return month+"";
    }

}
