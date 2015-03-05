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
import android.util.Log;
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

        SMS s= new SMS();
        s.text = smsMessage[0].getMessageBody();
        s.findSMS();

            if(s.findSMS() &&  s.amount !=null) {

                final NotificationManager mgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification note = new Notification(R.drawable.ic_shopping_basket_grey600_24dp,
                        "MyWallet",
                        System.currentTimeMillis());

                // This pending intent will open after notification click
                PendingIntent i = PendingIntent.getActivity(context, 0,
                        new Intent(context, Expense_add_window.class),
                        0);

                note.setLatestEventInfo(context, "New Expense",
                        "Rs." + s.amount + " - " + s.where, i);

                 SMS.syncSMS(context);

                //After uncomment this line you will see number of notification arrived
                //note.number=2;
                mgr.notify(678, note);//need to cleanup this hard coded value
            }
        else{
                if (BuildConfig.DEBUG) {
                    Log.e(Constants.TAG, "Invalid SMS: "+s.text);
                }
            }

    }
    public interface Constants {
        String TAG = "app:SMSListener";
    }
}
