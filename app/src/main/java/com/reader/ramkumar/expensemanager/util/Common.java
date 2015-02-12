package com.reader.ramkumar.expensemanager.util;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Ramkumar on 12/02/15.
 */
public class Common {
    public static String CURRENCY =  Currency.getInstance(Locale.getDefault()).getSymbol();
}
