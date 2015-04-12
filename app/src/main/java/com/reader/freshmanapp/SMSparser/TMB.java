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
 Ur Acc#302689 debited with Rs.1,000.00 for ATM Txn. Card#7036. Ref#328186. Acc Bal Rs.+316.Customer Care:044-26223106,26223109-TMB

 Ur Acc#302689 Debited with Rs.5,000.00 for Txn at ATM#TMB00401 Card#xxxx7036. Ref#491384. Dt:07-02-15. Remaining Bal in Acc Rs.+5557

 Ur Acc 302689 is debited with Rs.249.00 for Txn at MC DONALDS, Card#xxxx7036. Ref#518258. Dt:26-01-15. Remaining Bal in Acc Rs.+3557
 */
public class TMB extends Master{
    /*sms template needs to be parsed */
    final String[][] template = {
            {"Ur Acc (.*) is debited with Rs.(.*) for Txn at (.*), Card#(.*).", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Ur Acc#(.*) debited with Rs.(.*) for ATM Txn. Card#(.*). Ref", TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Ur Acc#(.*) Debited with Rs.(.*) for Txn at ATM#(.*) Card#(.*).", TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()}
    };
    /*0-Amount,1-Account,2-Time,3-Where,4-Place/city,5-remaining cash in card,9-Ignore*/
    final int[][] templateMap = { //the numbers are the curresponding values in the template*/
            {1, 0, 3},
            {1, 0, 3},
            {1, 0, 3}
    };
    /*this class variables and name needs to be same for all other banks*/
    public SMSParserData parserValue;
    String sms;
    public TMB(String text) {
        this.sms = text.replace("/ +/g", " "); // replaces multiple spaces to one space
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Actual SMS: " + text);
            Log.e(Constants.TAG, "Trimmed SMS: " + this.sms);

        }
    }

    public interface Constants {
        String TAG = "app:TMB";
    }

}

