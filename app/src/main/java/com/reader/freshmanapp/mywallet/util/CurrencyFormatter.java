package com.reader.freshmanapp.mywallet.util;

/**
 * Created by Ramkumar on 15/03/15.
 */

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * This ValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
public class CurrencyFormatter implements ValueFormatter {

    protected DecimalFormat mFormat;

    public CurrencyFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return Common.CURRENCY + " " + mFormat.format(value) ;
    }
}
