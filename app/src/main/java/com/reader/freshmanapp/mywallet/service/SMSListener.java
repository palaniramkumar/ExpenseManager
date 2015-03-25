package com.reader.freshmanapp.mywallet.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.reader.freshmanapp.SMSparser.SMS;
import com.reader.freshmanapp.mywallet.*;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.Common;

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

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        final boolean isGPS=prefs.getBoolean("gps", false);
        Location location=null;
        if(isGPS) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";
                Log.d("gps", "lat :  " + latitude);
                Log.d("gps", "long :  " + longitude);

            }
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

                // This pending intent will open after notification click
                PendingIntent i = PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class),
                        0);

                DBHelper db= new DBHelper(context);

                if(location!=null)
                    SMS.syncSMS(context,latitude+","+longitude);
                else
                    SMS.syncSMS(context);

                if (BuildConfig.DEBUG) {
                    Log.e(Constants.TAG, "Called SYNS SMS ");
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

                mBuilder.setContentTitle(Common.CURRENCY + s.amount + " at " + s.where)
                        .setSmallIcon(R.drawable.ic_shopping_basket_grey600_24dp)
                        .addAction(R.drawable.ic_action_share, "Share", i)
                        .setContentIntent(i)
                        .setAutoCancel(true)
                        .setContentText("This month expense " + Common.CURRENCY + db.getMyTotalExpense());

                final boolean isBudget = prefs.getBoolean("budget", false);
                if(isBudget)
                    mBuilder.setProgress(db.getBudget(),(int) db.getMyTotalExpense(), false);

                mgr.notify(0, mBuilder.build());

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
