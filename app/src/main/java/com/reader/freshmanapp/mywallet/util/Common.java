package com.reader.freshmanapp.mywallet.util;

import android.graphics.Color;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Ramkumar on 12/02/15.
 */
public class Common {
    public static final int[] MATERIAL_COLORS = {
            Color.rgb(244, 67, 54), Color.rgb(233, 30, 99), Color.rgb(156, 39, 176),
            Color.rgb(103, 58, 183), Color.rgb(63, 81, 181), Color.rgb(33, 150, 243),
            Color.rgb(0, 188, 212), Color.rgb(0, 150, 136), Color.rgb(0, 150, 136), Color.rgb(0, 150, 136),
            Color.rgb(255, 87, 34), Color.rgb(121, 85, 72), Color.rgb(158, 158, 158), Color.rgb(96, 125, 139)

    };
    public static String CURRENCY = getLocalCurrency();

    private static String getLocalCurrency() {
        String loc_currency = Currency.getInstance(Locale.getDefault()).getSymbol();
        if (loc_currency.contains("Rs"))
            loc_currency = "₹";
        return loc_currency;

    }
}
