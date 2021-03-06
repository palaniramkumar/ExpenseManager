package com.reader.freshmanapp.SMSparser;

import android.util.Log;

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.util.TYPES;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ram on 04/01/2015.
 * template
 * ========
 * <p/>
 * Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on 2015-01-01:12:39:35 at (.*?).Avl bal - Rs.1234.00, curr o/s - Rs.123.00
 * <p/>
 * An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking.
 * <p/>
 * INR (.*?) deposited to A/c No (.*?) towards NEFT Cr-CITI0000004-AON SERVICES I PL-GROSS MODEL A/C-RAMKUMAR K PALANI-CITIN14502045258 Val 31-DEC-14. Clr Bal is INR (.*?) subject to clearing.
 * <p/>
 * Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on 2015-01-02:16:04:16.
 * <p/>
 * Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on 08/JAN/2015 .
 * <p/>
 * 01-03-2015: Rs.(.*?) was withdrawn using your HDFC Bank Card ending (.*?) on (.*?) at (.*?). Avl bal: (.*?)
 */
public class HDFC extends Master{
    /*sms template needs to be parsed */
    public final String[][] template = {
            {"Rs.(.*?) was spent on ur HDFCBank CREDIT Card ending (.*?) on (.*?) at (.*?).Avl", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.CREDIT_CARD.toString()},
            {"An amount of Rs.(.*?) has been debited from your account number (.*?) for (.*?) done using HDFC Bank NetBanking", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()},
            {"An amount of Rs.(.*?) has been debited from your account  number (.*?) for (.*?) done using HDFC Bank NetBanking", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()},
            {"INR (.*?) deposited to A/c No (.*?) towards (.*?) Val ", TYPES.TRANSACTION_TYPE.INCOME.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()},
            {"Thank you for using your HDFC Bank DEBIT/ATM Card ending (.*?) for Rs. (.*?) towards ATM WDL in (.*?) at (.*?) on (.*?)", TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Rs.(.*?) was withdrawn using your HDFC Bank Card ending (.*?) on (.*?) at (.*?). Avl bal: (.*?)", TYPES.TRANSACTION_TYPE.CASH_VAULT.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"Your BANK a/c xxxx (.*?) will be debited for Rs (.*?) towards (.*?) on (.*?)", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.DEBIT_CARD.toString()},
            {"INR (.*?) Dr to A/c No (.*?) towards (.*?) Val ", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()},
            {"Your payment for (.*?) - (.*?) for Rs (.*?) has been processed successfully.", TYPES.TRANSACTION_TYPE.EXPENSE.toString(), TYPES.TRANSACTION_SOURCE.NET_BANKING.toString()}
    };
    /*0-Amount,1-Account,2-Time,3-Where,4-Place/city,5-remaining cash in card,9-Ignore*/
    public final int[][] templateMap = { //the numbers are the curresponding values in the template*/
            {0, 1, 2, 3},
            {0, 1, 3},
            {0, 1, 3},
            {0, 1, 3},
            {1, 0, 4, 3, 2},
            {0, 1, 2, 3, 5},
            {1, 0, 3, 2},
            {0, 1, 3},
            {3, 1, 0}
    };
    /*this class variables and name needs to be same for all other banks*/
    //public SMSParserData parserValue;
    String sms;
    public HDFC(String text) {
        this.sms = text.replace("/ +/g", " "); // replaces multiple spaces to one space
        if (BuildConfig.DEBUG) {
            Log.e(Constants.TAG, "Actual SMS: " + text);
            Log.e(Constants.TAG, "Trimmed SMS: " + this.sms);

        }
    }

    public interface Constants {
        String TAG = "app:HDFC";
    }


}

