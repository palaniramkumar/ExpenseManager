package com.reader.ramkumar.expensemanager.util;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Ramkumar on 12/02/15.
 */
public class Common {
    private static String  getLocalCurrency(){
        String loc_currency = Currency.getInstance(Locale.getDefault()).getSymbol();
        if(loc_currency.contains("Rs"))
            loc_currency = "₹";
        return loc_currency;
        
    }
    public static String CURRENCY =  getLocalCurrency();
}
