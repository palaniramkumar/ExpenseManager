package com.reader.freshmanapp.mywallet.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.reader.freshmanapp.SMSparser.SMS;
import com.reader.freshmanapp.mywallet.BuildConfig;
import com.reader.freshmanapp.mywallet.MainActivity;
import com.reader.freshmanapp.mywallet.R;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.Common;
import com.reader.freshmanapp.mywallet.util.TYPES;

/**
 * Created by Ram on 08/01/2015.
 */
public class SMSListener extends BroadcastReceiver {
    /**
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    private long getLstSMSIndex(Context context){
        Uri uriSms = Uri.parse("content://sms/inbox");
        //understanding that only one SMS can be recieved at the time and maked the last SMS ID as this SMS id
        final Cursor cursor =context.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,"date desc limit 1");
        if(cursor.moveToNext())
            return cursor.getLong(0);
        return -1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        /*debug : get last sms from sms object
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] {"body", "address"},
                null, null, "date desc limit 1");

        while (cursor.moveToNext()) {
            Toast.makeText(context, cursor.getString(0)+":"+ cursor.getString(1), Toast.LENGTH_SHORT).show();

        }*/

        String latitude = "", longitude = "",gps=null;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        final boolean isGPS = prefs.getBoolean("gps", false);
        Location location = null;
        if (isGPS) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";
                gps = latitude + "," + longitude;
                Log.d("gps", "lat :  " + latitude);
                Log.d("gps", "long :  " + longitude);

            }
        }

        //read incoming sms

        Bundle bundle = intent.getExtras();

        /** getting latest SMS **/
        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        SMS s = new SMS();
        s.text = smsMessage[0].getMessageBody();
        s.address = smsMessage[0].getOriginatingAddress();
        s.findSMS(context);

        if (s.findSMS(context) && s.amount != null) {

            final NotificationManager mgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // This pending intent will open after notification click
            PendingIntent i = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class),
                    0);

            DBHelper db = new DBHelper(context);

            s.address=smsMessage[0].getOriginatingAddress();
            s.text=smsMessage[0].getMessageBody();
            long ts = smsMessage[0].getTimestampMillis();
            s.id=(getLstSMSIndex(context)+smsMessage.length)+""; //calculate new sms id
            Log.e("Identifed SMS id",s.id);
            Log.e("count(sms)",smsMessage.length+"");
            s.when = db.getDroidDate(ts/1000) ;
            s.text = smsMessage[0].getMessageBody();
            s.findSMS(context);


            db.insertMaster(s.amount,s.bankName,s.trans_src,s.trans_type,s.expanse_category,s.where,s.id,s.where,s.when,
                    db.getNow(),s.place,gps,null,null, TYPES.TRANSACTION_STATUS.APPROVED.toString());


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            mBuilder.setContentTitle(Common.CURRENCY + s.amount + " at " + s.where)
                    .setSmallIcon(R.drawable.ic_shopping_basket_grey600_24dp)
                    .addAction(R.drawable.ic_action_share, "Share", i)
                    .setContentIntent(i)
                    .setAutoCancel(true)
                    .setContentText("This month expense " + Common.CURRENCY + db.getMyTotalExpense(db.month));

            final boolean isBudget = prefs.getBoolean("budget", false);
            if (isBudget)
                mBuilder.setProgress(db.getBudget(), (int) db.getMyTotalExpense(db.month), false);

            mgr.notify(0, mBuilder.build());

        } else {
            if (BuildConfig.DEBUG) {
                Log.e(Constants.TAG, "Invalid SMS: " + s.text);
            }
        }

    }

    public interface Constants {
        String TAG = "app:SMSListener";
    }
}
