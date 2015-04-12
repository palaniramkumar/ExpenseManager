package com.reader.freshmanapp.SMSparser;

import android.util.Log;

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.util.TYPES;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ram on 11/04/2015.
 * template
 * ========
 Rs.6000 withdrawn via ATM from a/c XX4807. The balance is now Rs.164474.41. Non-Citi ATM usage this month: Metro 0, Non-Metro 1. Charges may apply as per TnC

 Your request on 31/03/15 to pay Rs 1300 from A/C X1449 to RAVE-SHASTRI was accepted. Ref No CITIN15528701323. The a/c balance is now Rs 15086.71

 */
public class CITI extends Master{
    /*sms template needs to be parsed */
    final String[][] template = {
            {"Your request on (.*) to pay Rs (.*) from A/C (.*) to (.*) was accepted.", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()},
            {"Rs.(.*) withdrawn via ATM from a/c (.*). The balance is now Rs.(.*).", TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
    };
    /*0-Amount,1-Account,2-Time,3-Where,4-Place/city,5-remaining cash in card,9-Ignore*/
    final int[][] templateMap = { //the numbers are the curresponding values in the template*/
            {2, 0, 1, 4},
            {0, 1}
    };
    /*this class variables and name needs to be same for all other banks*/
    public SMSParserData parserValue;
    String sms;
    public CITI(String text) {
        this.sms = text.replace("/ +/g", " "); // replaces multiple spaces to one space
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Actual SMS: " + text);
            Log.e(Constants.TAG, "Trimmed SMS: " + this.sms);

        }
    }

    public interface Constants {
        String TAG = "app:CITI";
    }
}

