package com.reader.freshmanapp.mywallet.service;

/**
 * Created by Ramkumar on 19/02/15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.reader.freshmanapp.mywallet.MainActivity;
import com.reader.freshmanapp.mywallet.R;
import com.reader.freshmanapp.mywallet.db.DBHelper;
import com.reader.freshmanapp.mywallet.util.Common;

import java.util.Calendar;

public class SummaryReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PendingIntent i = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class),
                0);

        final NotificationManager mgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        DBHelper db=new DBHelper(context);
        int todayExpense = (int)Float.parseFloat(db.getExpensebyDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "", "%"));

        mBuilder.setContentTitle("MyWallet Summary")
                .setSmallIcon(R.drawable.ic_action_send)
                .setAutoCancel(true)
                .setContentIntent(i)
                .setContentText("Total spends today " + Common.CURRENCY + " " + todayExpense);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean isBudget = prefs.getBoolean("budget", false);
        if(isBudget)
            mBuilder.setProgress(db.getBudget(), (int)db.getMyTotalExpense(), false);


        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        Cursor cur = db.getMyExpenseByDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        while(cur.moveToNext()) {
            inboxStyle.addLine(cur.getString(0) +" "+Common.CURRENCY+" "+cur.getString(1));
        }
        mBuilder.setStyle(inboxStyle);

        mBuilder.addAction(R.drawable.ic_action_share, "Share", i);

        mgr.notify(0, mBuilder.build());
    }
}
