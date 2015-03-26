package com.reader.freshmanapp.mywallet.util;

import com.reader.freshmanapp.mywallet.db.DBHelper;

/**
 * Created by Ramkumar on 04/03/15.
 */
public class CashVault {
    public int vault_amount;
    public int spent_amount;
    public int amount_left;
    public int amount_left_in_percent;
    public String msg;

    public CashVault(DBHelper db) {
        vault_amount = db.getCashVault();
        spent_amount = db.getCashExpense();

        //calculating amount left in hand
        amount_left = vault_amount - spent_amount;
        if (vault_amount == 0)
            amount_left_in_percent = 0;
        else
            amount_left_in_percent = amount_left * 100 / vault_amount;

        if (amount_left_in_percent < 10)
            msg = "Please withdraw amount from ATM. You have just " + Common.CURRENCY + amount_left + " in cash vault";
        else if (amount_left_in_percent < 25)
            msg = "Your cash is really low. You have just " + Common.CURRENCY + amount_left + " in cash vault";
        else if (amount_left_in_percent < 50)
            msg = "You have " + Common.CURRENCY + amount_left + " in cash vault";
        else
            msg = "You have enough money " + Common.CURRENCY + amount_left + " in cash vault";
    }
}
