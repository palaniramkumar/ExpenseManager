package com.reader.ramkumar.expensemanager.service;

/**
 * Created by Ramkumar on 19/02/15.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.reader.ramkumar.expensemanager.Expense_add_window;
import com.reader.ramkumar.expensemanager.R;
import com.reader.ramkumar.expensemanager.db.DBHelper;
import com.reader.ramkumar.expensemanager.util.Common;

import java.util.Calendar;

public class SummaryReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final NotificationManager mgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher,"Today's Expense Summary", System.currentTimeMillis());
        PendingIntent i = PendingIntent.getActivity(context, 0,
                new Intent(context, Expense_add_window.class),
                0);
        DBHelper db=new DBHelper(context);
        notification.setLatestEventInfo(context, "Total Expense", "Amount "+ Common.CURRENCY+" "+db.getExpensebyDay(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+""), i);
        mgr.notify(0, notification);

    }
}
