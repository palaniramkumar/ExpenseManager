package com.reader.ramkumar.expensemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;
import com.reader.ramkumar.expensemanager.*;
import com.reader.ramkumar.SMSparser.HDFC;

/**
 * Created by Ram on 08/01/2015.
 */
public class SMSListener extends BroadcastReceiver{
    HDFC.SMSParserData strParse;

    /**
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        String text  = smsMessage[0].getMessageBody();
        if(text.contains("HDFC")){
            HDFC bank=new HDFC(text);
            strParse = bank.parseSMS();
            String amount=strParse.valueSet[0];
            String where = strParse.valueSet[3];
            //when = strParse[2];
            String place = strParse.valueSet[4];
            /*Intent intent1 = new Intent(context, Expense_add_window.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            Notification n  = new Notification.Builder(context)
                    .setContentTitle("New Expense Identified")
                    .setContentText("Rs."+amount + "in "+where)
                    .setSmallIcon(R.drawable.ic_action_cloud)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_action_edit, "View", pIntent)
                    .addAction(R.drawable.ic_action_star, "Accept", pIntent)
                    .addAction(R.drawable.ic_action_delete, "Delete", pIntent).build();*/
            if( amount !=null) {
                final NotificationManager mgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification note = new Notification(R.drawable.ic_action_cloud,
                        "Android Example Status message!",
                        System.currentTimeMillis());

                // This pending intent will open after notification click
                PendingIntent i = PendingIntent.getActivity(context, 0,
                        new Intent(context, Expense_add_window.class),
                        0);

                note.setLatestEventInfo(context, "New Expense Identified",
                        "Rs." + amount + " for " + where, i);

                //After uncomment this line you will see number of notification arrived
                //note.number=2;
                mgr.notify(678, note);
            }

        }
        // show first message
        Toast toast = Toast.makeText(context, "Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
        toast.show();
    }
}
