package com.reader.ramkumar.expensemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.reader.ramkumar.SMSparser.SMS;
import com.reader.ramkumar.expensemanager.*;
import com.reader.ramkumar.SMSparser.HDFC;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;
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
        String latitude="",longitude="";

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {

            latitude=location.getLatitude()+"";
            longitude=location.getLongitude()+"";
            Log.d("gps","lat :  "+latitude);
            Log.d("gps","long :  "+longitude);

        }




        Bundle bundle = intent.getExtras();

        /** getting latest SMS **/
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        SMS s= new SMS();
        s.text = smsMessage[0].getMessageBody();
        s.findSMS(context);

            if(s.findSMS(context) &&  s.amount !=null) {

                final NotificationManager mgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification note = new Notification(R.drawable.ic_shopping_basket_grey600_24dp,
                        "MyWallet",
                        System.currentTimeMillis());

                // This pending intent will open after notification click
                PendingIntent i = PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class),
                        0);

                DBHelper db= new DBHelper(context);
                note.setLatestEventInfo(context, Common.CURRENCY + s.amount + " at " + s.where,
                        "This month expense " + Common.CURRENCY + db.getMyTotalExpense(), i);
                if(location!=null)
                    SMS.syncSMS(context,latitude+","+longitude);
                else
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
