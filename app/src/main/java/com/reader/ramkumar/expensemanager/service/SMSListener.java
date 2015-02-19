package com.reader.ramkumar.expensemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.reader.ramkumar.SMSparser.SMS;
import com.reader.ramkumar.expensemanager.*;
import com.reader.ramkumar.SMSparser.HDFC;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.TYPES;

/**
 * Created by Ram on 08/01/2015.
 */
public class SMSListener extends BroadcastReceiver{
    private long getLstSMSIndex(Context context){
        Uri uriSms = Uri.parse("content://sms/inbox");
        //understanding that only one SMS can be recieved at the time and maked the last SMS ID as this SMS id
        final Cursor cursor =context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,"_id desc");


        if(cursor.moveToNext())
            return cursor.getLong(0);
        return -1;
    }

    /**
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        /** getting latest SMS **/
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        DBHelper db = new DBHelper(context);

        SMS s= new SMS();
        //parse details from the latest sms
        s.address=smsMessage[0].getOriginatingAddress();
        s.text=smsMessage[0].getMessageBody();
        long ts = smsMessage[0].getTimestampMillis();
        s.id=getLstSMSIndex(context)+"";
        s.when = db.getDroidDate(ts/1000) ; //convert android timestamp to SQL ts


            if(s.findSMS() &&  s.amount !=null) {

                final NotificationManager mgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification note = new Notification(R.drawable.ic_action_add_shopping_cart,
                        "MyWallet",
                        System.currentTimeMillis());

                // This pending intent will open after notification click
                PendingIntent i = PendingIntent.getActivity(context, 0,
                        new Intent(context, Expense_add_window.class),
                        0);

                note.setLatestEventInfo(context, "New Expense",
                        "Rs." + s.amount + " - " + s.where, i);
                db.insertMaster(s.amount,s.bankName,s.trans_src,s.trans_type,s.expanse_category,s.where,s.id,s.where,s.when,
                        db.getNow(),s.place,null,null,null, TYPES.TRANSACTION_STATUS.APPROVED.toString());
                //After uncomment this line you will see number of notification arrived
                //note.number=2;
                mgr.notify(678, note);//need to cleanup this hard coded value
            }


        // show first message
       // Toast toast = Toast.makeText(context, "Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
       // toast.show();
    }
}
