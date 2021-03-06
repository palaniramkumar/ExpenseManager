package com.reader.freshmanapp.SMSparser;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.db.DBCategoryMap;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.TYPES;

/**
 * Created by Ram on 04/01/2015.
 * SMS value setter from the bank object
 */
public class SMS {
    public String address;
    public String text;
    public String trans_src;
    public String id;

    public String trans_type;
    public String bankName;
    public String amount;
    public String where;
    public String when;
    public String place;
    public String account;
    public String expanse_category;

    public static boolean syncSMS(Context context) {
        return syncSMS(context, false, null);

    }

    public static boolean syncSMS(Context context, String gps) {
        return syncSMS(context, false, gps);

    }

    public static boolean syncSMS(Context context, boolean force, String gps) {


        DBHelper db = new DBHelper(context);
        int last_sms_id = db.getLastSMSID();

        String filter = null;
        if (!force)
            filter = "_id > " + last_sms_id;

        Uri uriSms = Uri.parse("content://sms/inbox");
        final Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, filter, null, null);

        //cursor.moveToFirst();


        while (cursor.moveToNext()) {
            //System.out.println("last id: " + last_sms_id + "; smsid:" + cursor.getString(0));
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            /* custom code*/
            final SMS s = new SMS();
            s.address = address;
            s.text = body;
            s.id = cursor.getString(0);
            s.when = db.getDroidDate(cursor.getLong(2) / 1000);

             /* this may need to tune further for better accurecy */
            if (s.findSMS(context) && s.amount != null) {
                //Add an object to the list

                db.insertMaster(s.amount.replace(",", ""), s.bankName, s.trans_src, s.trans_type, s.expanse_category, s.where, s.id, s.where, s.when,
                        db.getNow(), s.place, gps, null, null, s.account,TYPES.TRANSACTION_STATUS.APPROVED.toString());
            }

            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "amount =" + s.amount + "," + s.expanse_category+" , SMS = "+s.text + "Addr = "+s.address);
            }
        }
        db.close();
        return true;

    }

    public boolean isAvailable(String[] array, String val) {
        if (val != null)
            for (int i = 0; i < array.length; i++) {
                if (val.toLowerCase().contains(array[i].toLowerCase()))
                    return true;
            }
        return false;
    }

    public boolean findSMS(Context context) {
        if (address!=null ) { // add || text.contains("HDFC") code to test data with custom user SMS
        //if(text.contains("HDFC") ){
            Master.SMSParserData smsparsedata = null;
            if( address.contains("HDFC")) {
                bankName = "HDFC";
                HDFC bank = new HDFC(text);
                smsparsedata = bank.parseSMS(bank.template,bank.templateMap,bank.sms);
            }
            else if( address.contains("TMB")) {
                bankName = "TMB";
                TMB bank = new TMB(text);
                smsparsedata = bank.parseSMS(bank.template,bank.templateMap,bank.sms);
            }
            else if( address.contains("CITI")) {
                bankName = "CITI";
                CITI bank = new CITI(text);
                smsparsedata = bank.parseSMS(bank.template,bank.templateMap,bank.sms);
            }
            else
                return false;
            /*0-Amount,1-Account,2-Time,3-Where,4-Place*/
            amount = smsparsedata.valueSet[0];
            where = smsparsedata.valueSet[3];
            //when = smsparsedata.valueSet[2];
            place = smsparsedata.valueSet[4];
            account = smsparsedata.valueSet[1];
            trans_type = smsparsedata.trans_type;
            trans_src = smsparsedata.trans_src;
            expanse_category = DBHelper.UNCATEGORIZED;
            if (amount == null) return false;
            amount = amount.replace(",","");

            DBCategoryMap c = new DBCategoryMap(context);
            if (where !=null) {
                Cursor cur = c.getCategoryInfo(where);
                if (BuildConfig.DEBUG) {
                    Log.e(Constants.TAG, where);
                }
                if (cur.moveToNext()) {
                    expanse_category = cur.getString(2);
                    trans_type = cur.getString(3);
                    if (BuildConfig.DEBUG) {
                        Log.e(Constants.TAG, trans_type + "=" + expanse_category);
                    }
                }
            }
            if (trans_type.equals(TYPES.TRANSACTION_TYPE.CASH_VAULT.toString())) // code for ATM transaction
                expanse_category = DBHelper.ATM;

            return true;
        } else
            return false;
    }

    public interface Constants {
        String TAG = "app:SMS";
    }

}
